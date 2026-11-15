
package rdpl_timetable_backend.model;

public class SectionSubjectQuota {

	private String sectionId;

	private String className;

	private String subjectName;

	private String type;

	private int periodsPerDay;

	private int periodsPerWeek;

	public SectionSubjectQuota() {
	}

	public SectionSubjectQuota(String sectionId, String className, String subjectName, String type, int periodsPerDay,
			int periodsPerWeek) {

		this.sectionId = sectionId;
		this.className = className;
		this.subjectName = subjectName;
		this.type = type;
		this.periodsPerDay = periodsPerDay;
		this.periodsPerWeek = periodsPerWeek;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPeriodsPerDay() {
		return periodsPerDay;
	}

	public void setPeriodsPerDay(int periodsPerDay) {
		this.periodsPerDay = periodsPerDay;
	}

	public int getPeriodsPerWeek() {
		return periodsPerWeek;
	}

	public void setPeriodsPerWeek(int periodsPerWeek) {
		this.periodsPerWeek = periodsPerWeek;
	}
}
