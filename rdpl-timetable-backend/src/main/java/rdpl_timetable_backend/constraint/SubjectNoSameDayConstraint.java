
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
public class SubjectNoSameDayConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || variables.isEmpty()) {
			return;
		}

		Map<String, List<SchedulingVariable>> groups = variables.stream().collect(Collectors
				.groupingBy(v -> v.getSectionId() + "_" + v.getSubjectName() + "_" + v.getTimeSlot().getDay()));

		for (List<SchedulingVariable> dailyVariables : groups.values()) {

			if (dailyVariables.size() > 1) {

				LinearArgument[] arguments = dailyVariables.stream().map(SchedulingVariable::getVariable)
						.toArray(LinearArgument[]::new);

				model.addLessOrEqual(LinearExpr.sum(arguments), 1);
			}
		}
	}
}
