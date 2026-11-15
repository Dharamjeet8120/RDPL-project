
package rdpl_timetable_backend.constraint;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class OffDayConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables, String entityId, List<String> offDays,
			boolean teacher) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || offDays == null) {
			throw new IllegalArgumentException("Variables or off days are missing.");
		}

		if (entityId == null || entityId.isBlank()) {
			throw new IllegalArgumentException("Entity ID is required.");
		}

		for (SchedulingVariable variable : variables) {

			boolean matchesEntity;

			if (teacher) {
				matchesEntity = entityId.equals(variable.getTeacherId());
			} else {
				matchesEntity = entityId.equals(variable.getSectionId());
			}

			if (matchesEntity && offDays.contains(variable.getTimeSlot().getDay())) {

				model.addEquality(variable.getVariable(), 0);
			}
		}
	}
}
