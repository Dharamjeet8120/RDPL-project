
package rdpl_timetable_backend.constraint;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class SectionNoDoubleBookingConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || variables.isEmpty()) {
			return;
		}

		for (SchedulingVariable current : variables) {

			String day = current.getTimeSlot().getDay();

			int periodNumber = current.getTimeSlot().getPeriodNumber();

			List<SchedulingVariable> sameSlotVariables = variables.stream().filter(
					v -> v.getTimeSlot().getDay().equals(day) && v.getTimeSlot().getPeriodNumber() == periodNumber)
					.toList();

			if (sameSlotVariables.size() > 1) {

				model.addLessOrEqual(LinearExpr.sum(sameSlotVariables.stream().map(SchedulingVariable::getVariable)
						.toArray(com.google.ortools.sat.Literal[]::new)), 1);
			}
		}
	}
}
