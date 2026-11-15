
package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;

import rdpl_timetable_backend.model.TimeSlot;

@Component
public class SchedulingVariableBuilder {

	public List<SchedulingVariable> buildVariables(CpModel model, String sectionId, String subjectName,
			String teacherId, String type, List<TimeSlot> timeSlots) {

		List<SchedulingVariable> variables = new ArrayList<>();

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (timeSlots == null || timeSlots.isEmpty()) {
			throw new IllegalArgumentException("Time slots are missing.");
		}

		for (TimeSlot timeSlot : timeSlots) {

			String variableName = sectionId + "_" + subjectName.replaceAll("[^a-zA-Z0-9]", "_") + "_"
					+ timeSlot.getDay() + "_P" + timeSlot.getPeriodNumber();

			IntVar variable = model.newBoolVar(variableName);

			SchedulingVariable schedulingVariable = new SchedulingVariable(sectionId, subjectName, teacherId, type,
					timeSlot, variable);

			variables.add(schedulingVariable);
		}

		return variables;
	}
}
