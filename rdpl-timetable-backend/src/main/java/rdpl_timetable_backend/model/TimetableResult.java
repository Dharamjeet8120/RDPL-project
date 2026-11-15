
package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;

public class TimetableResult {

	private String solverStatus;

	private double objectiveValue;

	private long solvingTimeMillis;

	private List<SectionTimetable> sectionTimetables = new ArrayList<>();

	private List<TeacherTimetable> teacherTimetables = new ArrayList<>();

	public TimetableResult() {
	}

	public String getSolverStatus() {
		return solverStatus;
	}

	public void setSolverStatus(String solverStatus) {
		this.solverStatus = solverStatus;
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

	public List<SectionTimetable> getSectionTimetables() {
		return sectionTimetables;
	}

	public void setSectionTimetables(List<SectionTimetable> sectionTimetables) {
		this.sectionTimetables = sectionTimetables;
	}

	public List<TeacherTimetable> getTeacherTimetables() {
		return teacherTimetables;
	}

	public void setTeacherTimetables(List<TeacherTimetable> teacherTimetables) {
		this.teacherTimetables = teacherTimetables;
	}
}
