
package rdpl_timetable_backend.model;

import java.util.List;

public class SectionsData {

	private String academicYear;
	private int weeklyTeachingCapacity;
	private int totalSections;
	private List<Section> sections;

	public SectionsData() {
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public int getWeeklyTeachingCapacity() {
		return weeklyTeachingCapacity;
	}

	public void setWeeklyTeachingCapacity(int weeklyTeachingCapacity) {
		this.weeklyTeachingCapacity = weeklyTeachingCapacity;
	}

	public int getTotalSections() {
		return totalSections;
	}

	public void setTotalSections(int totalSections) {
		this.totalSections = totalSections;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
}
