
package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;

import rdpl_timetable_backend.constraint.ConstraintManager;
import rdpl_timetable_backend.model.TimeSlot;

@Component
public class TimetableSolver {

	private final SchedulingVariableBuilder variableBuilder;

	private final ConstraintManager constraintManager;

	private final SolverExecutor solverExecutor;

	public TimetableSolver(SchedulingVariableBuilder variableBuilder, ConstraintManager constraintManager,
			SolverExecutor solverExecutor) {

		this.variableBuilder = variableBuilder;
		this.constraintManager = constraintManager;
		this.solverExecutor = solverExecutor;
	}

	public SolverResult solve(String sectionId, String subjectName, String teacherId, String type,
			List<TimeSlot> timeSlots) {

		CpModel model = new CpModel();

		List<SchedulingVariable> variables = new ArrayList<>();

		List<SchedulingVariable> subjectVariables = variableBuilder.buildVariables(model, sectionId, subjectName,
				teacherId, type, timeSlots);

		variables.addAll(subjectVariables);

		constraintManager.applyAll(model, variables);

		return solverExecutor.execute(model);
	}
}
