
package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.List;

import rdpl_timetable_backend.model.TimetableEntry;

public class SolverResult {

	private String status;
	private double objectiveValue;
	private long solvingTimeMillis;
	private boolean feasible;

	private List<TimetableEntry> timetable;

	public SolverResult() {
		this.timetable = new ArrayList<>();
	}

	public SolverResult(String status, double objectiveValue, long solvingTimeMillis, boolean feasible) {

		this.status = status;
		this.objectiveValue = objectiveValue;
		this.solvingTimeMillis = solvingTimeMillis;
		this.feasible = feasible;
		this.timetable = new ArrayList<>();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(double objectiveValue) {
		this.objectiveValue = objectiveValue;
	}

	public long getSolvingTimeMillis() {
		return solvingTimeMillis;
	}

	public void setSolvingTimeMillis(long solvingTimeMillis) {
		this.solvingTimeMillis = solvingTimeMillis;
	}

	public boolean isFeasible() {
		return feasible;
	}

	public void setFeasible(boolean feasible) {
		this.feasible = feasible;
	}

	public List<TimetableEntry> getTimetable() {
		return timetable;
	}

	public void setTimetable(List<TimetableEntry> timetable) {
		this.timetable = timetable;
	}
}
