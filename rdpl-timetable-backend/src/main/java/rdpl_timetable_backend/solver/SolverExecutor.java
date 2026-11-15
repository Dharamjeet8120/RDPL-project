
package rdpl_timetable_backend.solver;

import org.springframework.stereotype.Component;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;

@Component
public class SolverExecutor {

	public SolverResult execute(CpModel model) {

		if (model == null) {
			throw new IllegalArgumentException("CP-SAT model cannot be null.");
		}

		long startTime = System.currentTimeMillis();

		CpSolver solver = new CpSolver();

		solver.getParameters().setMaxTimeInSeconds(60.0);

		solver.getParameters().setNumSearchWorkers(8);

		CpSolverStatus status = solver.solve(model);

		long solvingTime = System.currentTimeMillis() - startTime;

		boolean feasible = status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE;

		double objectiveValue = feasible ? solver.objectiveValue() : 0.0;

		return new SolverResult(status.name(), objectiveValue, solvingTime, feasible);
	}
}
