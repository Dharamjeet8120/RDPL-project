
package rdpl_timetable_backend.constraint;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class SubjectPairingConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables, String sectionId, String subjectName) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || variables.isEmpty()) {
			return;
		}

		List<SchedulingVariable> subjectVariables = variables.stream()
				.filter(v -> sectionId.equals(v.getSectionId()) && subjectName.equals(v.getSubjectName())).toList();

		for (SchedulingVariable current : subjectVariables) {

			int periodNumber = current.getTimeSlot().getPeriodNumber();

			String day = current.getTimeSlot().getDay();

			SchedulingVariable next = subjectVariables.stream().filter(
					v -> v.getTimeSlot().getDay().equals(day) && v.getTimeSlot().getPeriodNumber() == periodNumber + 1)
					.findFirst().orElse(null);

			if (next != null) {

				model.addGreaterOrEqual(LinearExpr.sum(
						new com.google.ortools.sat.LinearArgument[] { current.getVariable(), next.getVariable() }), 0);
			}
		}
	}
}
