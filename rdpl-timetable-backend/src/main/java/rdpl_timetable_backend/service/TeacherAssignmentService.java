
package rdpl_timetable_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import rdpl_timetable_backend.loader.TeachersDataLoader;
import rdpl_timetable_backend.model.SectionSubjectQuota;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeacherAssignment;
import rdpl_timetable_backend.model.TeachersData;

@Service
public class TeacherAssignmentService {

	private static final Logger logger = LoggerFactory.getLogger(TeacherAssignmentService.class);

	private final TeachersDataLoader teachersDataLoader;

	public TeacherAssignmentService(TeachersDataLoader teachersDataLoader) {
		this.teachersDataLoader = teachersDataLoader;
	}

	public List<Teacher> findEligibleTeachers(String className, String subjectName, String type) {

		if (className == null || className.isBlank()) {
			throw new IllegalArgumentException("Class name is required.");
		}

		if (subjectName == null || subjectName.isBlank()) {
			throw new IllegalArgumentException("Subject name is required.");
		}

		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("Assignment type is required.");
		}

		TeachersData data = teachersDataLoader.loadTeachers();

		if (data == null || data.getTeachers() == null) {
			throw new IllegalStateException("Teacher data is missing.");
		}

		List<Teacher> eligibleTeachers = new ArrayList<>();

		boolean detailedDebug = matches(className, "Class 12") && matches(subjectName, "Games / Library Activity")
				&& matches(type, "ACTIVITY");

		logger.info("========== TEACHER MATCHING DEBUG ==========");
		logger.info("Requested Class   : {}", className);
		logger.info("Requested Subject : {}", subjectName);
		logger.info("Requested Type    : {}", type);

		for (Teacher teacher : data.getTeachers()) {

			if (teacher == null || teacher.getAssignments() == null) {
				continue;
			}

			boolean matched = false;

			for (TeacherAssignment assignment : teacher.getAssignments()) {

				if (assignment == null) {
					continue;
				}

				boolean classMatched = matches(assignment.getClassName(), className);

				boolean subjectMatched = matches(assignment.getSubjectOrActivity(), subjectName);

				boolean typeMatched = matches(assignment.getType(), type);

				boolean periodsValid = assignment.getPeriodsPerWeek() > 0;

				if (detailedDebug) {
					logger.info(
							"CHECK | Teacher: {} | Assignment Class: {} | Subject: {} | Type: {} | Periods: {} | ClassMatch: {} | SubjectMatch: {} | TypeMatch: {} | PeriodsValid: {}",
							teacher.getTeacherName(), assignment.getClassName(), assignment.getSubjectOrActivity(),
							assignment.getType(), assignment.getPeriodsPerWeek(), classMatched, subjectMatched,
							typeMatched, periodsValid);
				}

				if (classMatched && subjectMatched && typeMatched && periodsValid) {

					matched = true;

					logger.info("MATCH FOUND | Teacher: {} | Class: {} | Subject: {} | Type: {} | Periods: {}",
							teacher.getTeacherName(), className, subjectName, type, assignment.getPeriodsPerWeek());

					break;
				}
			}

			if (matched) {
				eligibleTeachers.add(teacher);
			}
		}

		logger.info("Total Eligible Teachers: {}", eligibleTeachers.size());
		logger.info("============================================");

		if (eligibleTeachers.isEmpty()) {
			logger.warn("NO ELIGIBLE TEACHER | Class: {} | Subject: {} | Type: {}", className, subjectName, type);
		}

		return eligibleTeachers;
	}

	private boolean matches(String source, String target) {

		if (source == null || target == null) {
			return false;
		}

		return normalize(source).equals(normalize(target));
	}

	private static String normalize(String value) {

		if (value == null) {
			return "";
		}

		return value.toLowerCase().replaceAll("\\s*/\\s*", "/").replaceAll("\\s+", " ").trim();
	}

	public boolean hasEligibleTeacher(String className, String subjectName, String type) {

		return !findEligibleTeachers(className, subjectName, type).isEmpty();
	}

	public List<SectionSubjectQuota> findUnassignedQuotas(List<SectionSubjectQuota> quotas) {

		if (quotas == null) {
			throw new IllegalArgumentException("Section quotas are missing.");
		}

		List<SectionSubjectQuota> unassigned = new ArrayList<>();

		for (SectionSubjectQuota quota : quotas) {

			if (quota == null || quota.getPeriodsPerWeek() <= 0) {
				continue;
			}

			boolean assigned = hasEligibleTeacher(quota.getClassName(), quota.getSubjectName(), quota.getType());

			if (!assigned) {
				unassigned.add(quota);
			}
		}

		return unassigned;
	}
}
