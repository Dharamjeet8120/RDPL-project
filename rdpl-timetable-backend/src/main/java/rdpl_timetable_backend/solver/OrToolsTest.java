
package rdpl_timetable_backend.solver;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class OrToolsTest {

	public static void main(String[] args) {

		// Load native OR-Tools libraries
		Loader.loadNativeLibraries();

		// Create CP-SAT model
		CpModel model = new CpModel();

		// Create variables: x and y (0 to 10)
		IntVar x = model.newIntVar(0, 10, "x");
		IntVar y = model.newIntVar(0, 10, "y");

		// Constraint: x + y <= 10
		model.addLessOrEqual(LinearExpr.sum(new IntVar[] { x, y }), 10);

		// Objective: Maximize 3x + 2y
		model.maximize(LinearExpr.weightedSum(new IntVar[] { x, y }, new long[] { 3, 2 }));

		// Solve model
		CpSolver solver = new CpSolver();

		CpSolverStatus status = solver.solve(model);

		// Display result
		if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {

			System.out.println("OR-Tools is working successfully!");
			System.out.println("X = " + solver.value(x));
			System.out.println("Y = " + solver.value(y));
			System.out.println("Objective = " + solver.objectiveValue());

		} else {
			System.out.println("No solution found!");
			System.out.println("Solver Status: " + status);
		}
	}
}
