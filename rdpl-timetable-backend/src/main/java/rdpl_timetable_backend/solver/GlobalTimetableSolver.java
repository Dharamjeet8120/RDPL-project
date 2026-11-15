

package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.Literal;

import rdpl_timetable_backend.model.SectionSubjectQuota;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeacherAssignment;
import rdpl_timetable_backend.model.TimeSlot;
import rdpl_timetable_backend.model.TimetableEntry;
import rdpl_timetable_backend.service.TeacherAssignmentService;

@Component
public class GlobalTimetableSolver {

	private static final Logger log = LoggerFactory.getLogger(GlobalTimetableSolver.class);

	private static final int MAX_PERIODS_PER_DAY = 10;
	private static final int MAX_SOLVE_SECONDS = 60;
	private static final int WORKERS = 8;

	private final TeacherAssignmentService teacherAssignmentService;

	public GlobalTimetableSolver(TeacherAssignmentService teacherAssignmentService) {
		this.teacherAssignmentService = teacherAssignmentService;
	}

	public SolverResult solve(List<SectionSubjectQuota> quotas, List<TimeSlot> timeSlots) {

		// 1. Validate input.
		if (quotas == null || quotas.isEmpty()) {
			throw new IllegalArgumentException("Section quotas are missing.");
		}

		if (timeSlots == null || timeSlots.isEmpty()) {
			throw new IllegalArgumentException("Time slots are missing.");
		}

		// 2. Validate teacher and section overload.
		validateOverloads(quotas, timeSlots);

		// 3. Validate all teacher assignments.
		validateAllTeacherAssignments(quotas);

		CpModel model = new CpModel();

		List<SchedulingVariable> variables = new ArrayList<>();

		Map<String, List<IntVar>> teacherWorkloadVariables = new HashMap<>();

		Map<String, Integer> teacherWorkloadLimits = new HashMap<>();

		int variableCounter = 0;

		// Teacher assignment periods are defined per section (curriculum periods).
		// A teacher may cover every section of the class, so the weekly cap is
		// periods x number of sections of that class.
		Map<String, java.util.Set<String>> sectionIdsByClass = new HashMap<>();
		for (SectionSubjectQuota q : quotas) {
			if (q == null || q.getSectionId() == null) {
				continue;
			}
			sectionIdsByClass.computeIfAbsent(normalize(q.getClassName()), k -> new java.util.HashSet<>()).add(q.getSectionId());
		}

		// 4. Create scheduling variables and fulfill weekly quotas.
		for (SectionSubjectQuota quota : quotas) {

			if (quota == null || quota.getPeriodsPerWeek() <= 0) {
				continue;
			}

			log.debug("========== QUOTA DEBUG ==========");
			log.debug("Section ID : {}", quota.getSectionId());
			log.debug("Class Name : {}", quota.getClassName());
			log.debug("Subject    : {}", quota.getSubjectName());
			log.debug("Type       : {}", quota.getType());

			List<Teacher> teachers = teacherAssignmentService.findEligibleTeachers(quota.getClassName(),
					quota.getSubjectName(), quota.getType());

			if (teachers == null || teachers.isEmpty()) {
				throw new IllegalStateException(
						"No eligible teacher found for " + quota.getClassName() + " - " + quota.getSubjectName());
			}

			List<IntVar> quotaVariables = new ArrayList<>();

			for (Teacher teacher : teachers) {

				if (teacher == null || teacher.getTeacherId() == null || teacher.getTeacherId().isBlank()) {
					continue;
				}

				int assignmentLimit = getAssignmentCapacity(teacher, quota)
						* Math.max(1, sectionIdsByClass.getOrDefault(normalize(quota.getClassName()), java.util.Set.of()).size());

				if (assignmentLimit <= 0) {
					continue;
				}

				String workloadKey = createWorkloadKey(teacher, quota);

				teacherWorkloadLimits.put(workloadKey, assignmentLimit);

				for (TimeSlot slot : timeSlots) {

					if (slot == null || slot.getDay() == null) {
						continue;
					}

					String variableName = "V_" + (++variableCounter);

					BoolVar variable = model.newBoolVar(variableName);

					SchedulingVariable schedulingVariable = new SchedulingVariable(quota.getSectionId(),
							quota.getSubjectName(), teacher.getTeacherId(), quota.getType(), slot, variable);

					quotaVariables.add(variable);
					variables.add(schedulingVariable);

					teacherWorkloadVariables.computeIfAbsent(workloadKey, key -> new ArrayList<>()).add(variable);
				}
			}

			if (quotaVariables.isEmpty()) {
				throw new IllegalStateException(
						"No scheduling variables created for " + quota.getClassName() + " - " + quota.getSubjectName());
			}

			model.addEquality(LinearExpr.sum(quotaVariables.toArray(new IntVar[0])), quota.getPeriodsPerWeek());
		}

		// 5. A section cannot have two subjects in one time slot.
		Map<String, List<IntVar>> sectionSlotMap = new HashMap<>();

		for (SchedulingVariable variable : variables) {

			String key = safe(variable.getSectionId()) + "|" + variable.getTimeSlot().getDay() + "|"
					+ variable.getTimeSlot().getPeriodNumber();

			sectionSlotMap.computeIfAbsent(key, k -> new ArrayList<>()).add(variable.getVariable());
		}

		for (List<IntVar> slotVariables : sectionSlotMap.values()) {

			if (slotVariables.size() > 1) {
				model.addAtMostOne(slotVariables.toArray(new Literal[0]));
			}
		}

		// 6. A teacher cannot teach two sections at the same time.
		Map<String, List<IntVar>> teacherSlotMap = new HashMap<>();

		for (SchedulingVariable variable : variables) {

			String key = variable.getTeacherId() + "|" + variable.getTimeSlot().getDay() + "|"
					+ variable.getTimeSlot().getPeriodNumber();

			teacherSlotMap.computeIfAbsent(key, k -> new ArrayList<>()).add(variable.getVariable());
		}

		for (List<IntVar> slotVariables : teacherSlotMap.values()) {

			if (slotVariables.size() > 1) {
				model.addAtMostOne(slotVariables.toArray(new Literal[0]));
			}
		}

		// 7. Teacher weekly assignment workload limit.
		for (Map.Entry<String, List<IntVar>> entry : teacherWorkloadVariables.entrySet()) {

			int maxPeriods = teacherWorkloadLimits.get(entry.getKey());

			model.addLessOrEqual(LinearExpr.sum(entry.getValue().toArray(new IntVar[0])), maxPeriods);
		}

		// 8. Teacher daily workload limit: maximum 8 periods.
		Map<String, List<IntVar>> teacherDailyMap = new HashMap<>();

		for (SchedulingVariable variable : variables) {

			String key = variable.getTeacherId() + "|" + variable.getTimeSlot().getDay();

			teacherDailyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(variable.getVariable());
		}

		for (List<IntVar> dailyVariables : teacherDailyMap.values()) {

			model.addLessOrEqual(LinearExpr.sum(dailyVariables.toArray(new IntVar[0])), MAX_PERIODS_PER_DAY);
		}

		// 9. Section daily workload limit: maximum 8 periods.
		Map<String, List<IntVar>> sectionDailyMap = new HashMap<>();

		for (SchedulingVariable variable : variables) {

			String key = safe(variable.getSectionId()) + "|" + variable.getTimeSlot().getDay();

			sectionDailyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(variable.getVariable());
		}

		for (List<IntVar> dailyVariables : sectionDailyMap.values()) {

			model.addLessOrEqual(LinearExpr.sum(dailyVariables.toArray(new IntVar[0])), MAX_PERIODS_PER_DAY);
		}

		// 10. Run CP-SAT solver.
		long startTime = System.currentTimeMillis();

		CpSolver solver = new CpSolver();

		solver.getParameters().setMaxTimeInSeconds(MAX_SOLVE_SECONDS);

		solver.getParameters().setNumSearchWorkers(WORKERS);

		CpSolverStatus status = solver.solve(model);

		log.info("========== CP-SAT DIAGNOSTICS ==========");
		log.info("Solver Status: {}", status);
		log.info("Total Variables: {}", variables.size());
		log.info("Total Weekly Slots: {}", timeSlots.size());

		if (status == CpSolverStatus.INFEASIBLE) {
			log.error("MODEL IS INFEASIBLE.");
			log.error("Check teacher clashes, teacher workload limits,");
			log.error("section daily limits and assignment capacities.");
		}

		log.info("========================================");

		long solvingTime = System.currentTimeMillis() - startTime;

		boolean feasible = status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE;

		SolverResult result = new SolverResult(status.name(), 0.0, solvingTime, feasible);

		// 11. Extract timetable.
		if (feasible) {

			List<TimetableEntry> timetable = new ArrayList<>();

			for (SchedulingVariable variable : variables) {

				if (solver.booleanValue((Literal) variable.getVariable())) {

					timetable.add(new TimetableEntry(variable.getSectionId(), variable.getSubjectName(),
							variable.getTeacherId(), variable.getType(), variable.getTimeSlot()));
				}
			}

			result.setTimetable(timetable);
		}

		log.info("========== SOLVER RESULT ==========");
		log.info("Status       : {}", status);
		log.info("Feasible     : {}", feasible);
		log.info("Solving Time : {} ms", solvingTime);
		log.info("===================================");

		return result;
	}

	// --------------------------------------------------
	// TEACHER AND SECTION OVERLOAD VALIDATION
	// --------------------------------------------------

	private void validateOverloads(List<SectionSubjectQuota> quotas, List<TimeSlot> timeSlots) {

		int weeklyCapacity = timeSlots.size();

		log.info("========== OVERLOAD VALIDATION ==========");
		log.info("Total Weekly Slots: {}", weeklyCapacity);

		// 1. Teacher overload check.
		Map<String, Integer> teacherLoads = new HashMap<>();

		Set<String> processedTeachers = new HashSet<>();

		for (SectionSubjectQuota quota : quotas) {

			if (quota == null || quota.getPeriodsPerWeek() <= 0) {
				continue;
			}

			List<Teacher> eligibleTeachers = teacherAssignmentService.findEligibleTeachers(quota.getClassName(),
					quota.getSubjectName(), quota.getType());

			if (eligibleTeachers == null) {
				continue;
			}

			for (Teacher teacher : eligibleTeachers) {

				if (teacher == null || teacher.getTeacherId() == null || teacher.getTeacherId().isBlank()) {
					continue;
				}

				String teacherId = teacher.getTeacherId();

				if (!processedTeachers.add(teacherId)) {
					continue;
				}

				if (teacher.getAssignments() == null) {
					continue;
				}

				int totalLoad = 0;

				for (TeacherAssignment assignment : teacher.getAssignments()) {

					if (assignment == null || assignment.getPeriodsPerWeek() <= 0) {
						continue;
					}

					totalLoad += assignment.getPeriodsPerWeek();
				}

				teacherLoads.put(teacherId, totalLoad);
			}
		}

		for (Map.Entry<String, Integer> entry : teacherLoads.entrySet()) {

			if (entry.getValue() > weeklyCapacity) {

				log.warn("OVERLOADED TEACHER {} : {} > {}", entry.getKey(), entry.getValue(), weeklyCapacity);
			}
		}

		// 2. Section overload check.
		Map<String, Integer> sectionLoads = new HashMap<>();

		for (SectionSubjectQuota quota : quotas) {

			if (quota == null || quota.getPeriodsPerWeek() <= 0) {
				continue;
			}

			String sectionId = safe(quota.getSectionId());

			sectionLoads.merge(sectionId, quota.getPeriodsPerWeek(), Integer::sum);
		}

		for (Map.Entry<String, Integer> entry : sectionLoads.entrySet()) {

			if (entry.getValue() > weeklyCapacity) {

				log.warn("OVERLOADED SECTION {} : {} > {}", entry.getKey(), entry.getValue(), weeklyCapacity);
			}
		}

		log.info("Teacher overload checks completed: {}", teacherLoads.size());

		log.info("Section overload checks completed: {}", sectionLoads.size());

		log.info("=========================================");
	}

	// --------------------------------------------------
	// ASSIGNMENT CAPACITY
	// --------------------------------------------------

	private int getAssignmentCapacity(Teacher teacher, SectionSubjectQuota quota) {

		if (teacher.getAssignments() == null) {
			return 0;
		}

		int capacity = 0;

		for (TeacherAssignment assignment : teacher.getAssignments()) {

			if (assignment == null) {
				continue;
			}

			boolean sameClass = normalize(assignment.getClassName()).equals(normalize(quota.getClassName()));

			boolean sameSubject = normalize(assignment.getSubjectOrActivity())
					.equals(normalize(quota.getSubjectName()));

			boolean sameType = normalize(assignment.getType()).equals(normalize(quota.getType()));

			if (sameClass && sameSubject && sameType && assignment.getPeriodsPerWeek() > 0) {

				capacity += assignment.getPeriodsPerWeek();
			}
		}

		return capacity;
	}

	// --------------------------------------------------
	// WORKLOAD KEY
	// --------------------------------------------------

	private String createWorkloadKey(Teacher teacher, SectionSubjectQuota quota) {

		return teacher.getTeacherId() + "|" + normalize(quota.getClassName()) + "|" + normalize(quota.getSubjectName())
				+ "|" + normalize(quota.getType());
	}

	// --------------------------------------------------
	// NORMALIZATION
	// --------------------------------------------------

	private String normalize(String value) {

		if (value == null) {
			return "";
		}

		return value.replaceAll("\\s+", "").trim().toLowerCase();
	}

	private String safe(String value) {
		return value == null ? "" : value;
	}

	// --------------------------------------------------
	// VALIDATE ALL TEACHER ASSIGNMENTS
	// --------------------------------------------------

	private void validateAllTeacherAssignments(List<SectionSubjectQuota> quotas) {

		List<String> missingAssignments = new ArrayList<>();

		log.info("========== TEACHER ASSIGNMENT PRE-VALIDATION ==========");

		for (SectionSubjectQuota quota : quotas) {

			if (quota == null || quota.getPeriodsPerWeek() <= 0) {
				continue;
			}

			List<Teacher> eligibleTeachers = teacherAssignmentService.findEligibleTeachers(quota.getClassName(),
					quota.getSubjectName(), quota.getType());

			if (eligibleTeachers == null || eligibleTeachers.isEmpty()) {

				String missing = String.format(
						"Section: %s | Class: %s | Subject: %s | Type: %s | Required Periods: %d", quota.getSectionId(),
						quota.getClassName(), quota.getSubjectName(), quota.getType(), quota.getPeriodsPerWeek());

				missingAssignments.add(missing);
			}
		}

		if (!missingAssignments.isEmpty()) {

			log.error("Total Missing Assignments: {}", missingAssignments.size());

			missingAssignments.forEach(log::error);

			String errorMessage = "TEACHER ASSIGNMENT VALIDATION FAILED\n" + "Total Missing Assignments: "
					+ missingAssignments.size() + "\n\n" + String.join("\n", missingAssignments);

			throw new IllegalStateException(errorMessage);
		}

		log.info("All teacher assignments are valid.");
		log.info("========================================================");
	}
}
