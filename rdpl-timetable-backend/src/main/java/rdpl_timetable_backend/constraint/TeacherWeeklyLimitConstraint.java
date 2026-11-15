
package rdpl_timetable_backend.constraint;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class TeacherWeeklyLimitConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables, String teacherId, int maxPeriodsPerWeek) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || maxPeriodsPerWeek < 0) {
			throw new IllegalArgumentException("Invalid teacher weekly limit input.");
		}

		if (teacherId == null || teacherId.isBlank()) {
			throw new IllegalArgumentException("Teacher ID is required.");
		}

		List<SchedulingVariable> teacherVariables = variables.stream().filter(v -> teacherId.equals(v.getTeacherId()))
				.toList();

		model.addLessOrEqual(LinearExpr.sum(teacherVariables.stream().map(SchedulingVariable::getVariable)
				.toArray(com.google.ortools.sat.Literal[]::new)), maxPeriodsPerWeek);
	}
}
