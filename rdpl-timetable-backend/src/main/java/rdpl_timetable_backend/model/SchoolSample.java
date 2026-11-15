package rdpl_timetable_backend.model;

import java.util.List;
import java.util.Map;

public class SchoolSample {

	private boolean sampleMode;
	private String schoolName;
	private String academicYear;
	private List<String> workingDays;
	private int weeklyTeachingCapacity;

	private List<Map<String, Object>> sections;
	private List<Map<String, Object>> teachers;

	private List<Object> hardConstraints;
	private List<Object> softConstraints;
	private Map<String, Object> sharedTeachers;

	public SchoolSample() {
	}

	public boolean isSampleMode() {
		return sampleMode;
	}

	public void setSampleMode(boolean sampleMode) {
		this.sampleMode = sampleMode;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public List<String> getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(List<String> workingDays) {
		this.workingDays = workingDays;
	}

	public int getWeeklyTeachingCapacity() {
		return weeklyTeachingCapacity;
	}

	public void setWeeklyTeachingCapacity(int weeklyTeachingCapacity) {
		this.weeklyTeachingCapacity = weeklyTeachingCapacity;
	}

	public List<Map<String, Object>> getSections() {
		return sections;
	}

	public void setSections(List<Map<String, Object>> sections) {
		this.sections = sections;
	}

	public List<Map<String, Object>> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<Map<String, Object>> teachers) {
		this.teachers = teachers;
	}

	public List<Object> getHardConstraints() {
		return hardConstraints;
	}

	public void setHardConstraints(List<Object> hardConstraints) {
		this.hardConstraints = hardConstraints;
	}

	public List<Object> getSoftConstraints() {
		return softConstraints;
	}

	public void setSoftConstraints(List<Object> softConstraints) {
		this.softConstraints = softConstraints;
	}

	public Map<String, Object> getSharedTeachers() {
		return sharedTeachers;
	}

	public void setSharedTeachers(Map<String, Object> sharedTeachers) {
		this.sharedTeachers = sharedTeachers;
	}
}