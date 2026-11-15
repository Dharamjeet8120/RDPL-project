
package rdpl_timetable_backend.constraint;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class TeacherNoDoubleBookingConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || variables.isEmpty()) {
			return;
		}

		Map<String, List<SchedulingVariable>> teacherSlotGroups = variables.stream()
				.filter(v -> v.getTeacherId() != null && !v.getTeacherId().isBlank())
				.collect(Collectors.groupingBy(v -> v.getTeacherId() + "_" + v.getTimeSlot().getDay() + "_"
						+ v.getTimeSlot().getPeriodNumber()));

		for (List<SchedulingVariable> sameTeacherSlot : teacherSlotGroups.values()) {

			if (sameTeacherSlot.size() > 1) {

				model.addLessOrEqual(LinearExpr.sum(sameTeacherSlot.stream().map(SchedulingVariable::getVariable)
						.toArray(com.google.ortools.sat.Literal[]::new)), 1);
			}
		}
	}
}
