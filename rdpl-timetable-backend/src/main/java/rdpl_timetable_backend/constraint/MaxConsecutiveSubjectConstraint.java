
package rdpl_timetable_backend.constraint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearArgument;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class MaxConsecutiveSubjectConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || variables.isEmpty()) {
			return;
		}

		Map<String, List<SchedulingVariable>> subjectGroups = variables.stream().collect(Collectors
				.groupingBy(v -> v.getSectionId() + "_" + v.getSubjectName() + "_" + v.getTimeSlot().getDay()));

		for (List<SchedulingVariable> subjectVariables : subjectGroups.values()) {

			Map<Integer, SchedulingVariable> periodMap = subjectVariables.stream()
					.collect(Collectors.toMap(v -> v.getTimeSlot().getPeriodNumber(), v -> v));

			for (int period = 1; period <= 10; period++) {

				SchedulingVariable first = periodMap.get(period);

				SchedulingVariable second = periodMap.get(period + 1);

				SchedulingVariable third = periodMap.get(period + 2);

				SchedulingVariable fourth = periodMap.get(period + 3);

				if (first != null && second != null && third != null && fourth != null) {

					LinearArgument[] arguments = { first.getVariable(), second.getVariable(), third.getVariable(),
							fourth.getVariable() };

					model.addLessOrEqual(LinearExpr.sum(arguments), 3);
				}
			}
		}
	}
}
