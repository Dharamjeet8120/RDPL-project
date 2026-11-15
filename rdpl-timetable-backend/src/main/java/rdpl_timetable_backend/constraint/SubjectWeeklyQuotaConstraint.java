
package rdpl_timetable_backend.constraint;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;

import rdpl_timetable_backend.solver.SchedulingVariable;

@Component
public class SubjectWeeklyQuotaConstraint {

	public void apply(CpModel model, List<SchedulingVariable> variables, String sectionId, String subjectName,
			int requiredPeriodsPerWeek) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		if (variables == null || requiredPeriodsPerWeek < 0) {
			throw new IllegalArgumentException("Invalid weekly quota input.");
		}

		List<SchedulingVariable> subjectVariables = variables.stream()
				.filter(v -> sectionId.equals(v.getSectionId()) && subjectName.equals(v.getSubjectName())).toList();

		if (subjectVariables.isEmpty() && requiredPeriodsPerWeek > 0) {
			throw new IllegalStateException("No variables found for subject: " + subjectName);
		}

		model.addEquality(LinearExpr.sum(subjectVariables.stream().map(SchedulingVariable::getVariable)
				.toArray(com.google.ortools.sat.Literal[]::new)), requiredPeriodsPerWeek);
	}
}
