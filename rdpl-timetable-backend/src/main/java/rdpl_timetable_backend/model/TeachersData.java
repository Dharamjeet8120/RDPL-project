
package rdpl_timetable_backend.model;

import java.util.List;

public class TeachersData {

	private String academicYear;

	private int totalTeachers;

	private int assignedTeachers;

	private int unassignedTeachers;

	private List<Teacher> teachers;

	public TeachersData() {
	}

	public TeachersData(String academicYear, int totalTeachers, int assignedTeachers, int unassignedTeachers,
			List<Teacher> teachers) {

		this.academicYear = academicYear;
		this.totalTeachers = totalTeachers;
		this.assignedTeachers = assignedTeachers;
		this.unassignedTeachers = unassignedTeachers;
		this.teachers = teachers;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public int getTotalTeachers() {
		return totalTeachers;
	}

	public void setTotalTeachers(int totalTeachers) {
		this.totalTeachers = totalTeachers;
	}

	public int getAssignedTeachers() {
		return assignedTeachers;
	}

	public void setAssignedTeachers(int assignedTeachers) {
		this.assignedTeachers = assignedTeachers;
	}

	public int getUnassignedTeachers() {
		return unassignedTeachers;
	}

	public void setUnassignedTeachers(int unassignedTeachers) {
		this.unassignedTeachers = unassignedTeachers;
	}

	public List<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<Teacher> teachers) {
		this.teachers = teachers;
	}
}
