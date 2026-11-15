
package rdpl_timetable_backend.constraint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class SectionDailyLimitConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables, String sectionId, int maxPeriodsPerDay) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || maxPeriodsPerDay < 0) {
			throw new IllegalArgumentException("Invalid section daily limit input.");
		}

		if (sectionId == null || sectionId.isBlank()) {
			throw new IllegalArgumentException("Section ID is required.");
		}

		Map<String, List<SchedulingVariable>> dailyGroups = variables.stream()
				.filter(v -> sectionId.equals(v.getSectionId()))
				.collect(Collectors.groupingBy(v -> v.getTimeSlot().getDay()));

		for (List<SchedulingVariable> dailyVariables : dailyGroups.values()) {

			model.addLessOrEqual(LinearExpr.sum(dailyVariables.stream().map(SchedulingVariable::getVariable)
					.toArray(com.google.ortools.sat.Literal[]::new)), maxPeriodsPerDay);
		}
	}
}
