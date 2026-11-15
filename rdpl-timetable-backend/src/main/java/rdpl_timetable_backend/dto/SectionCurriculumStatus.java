
package rdpl_timetable_backend.dto;

public class SectionCurriculumStatus {

	private String sectionId;
	private String className;
	private String sectionName;
	private String curriculumKey;
	private String status;
	private Integer weeklyPeriods;

	public SectionCurriculumStatus() {
	}

	public SectionCurriculumStatus(String sectionId, String className, String sectionName, String curriculumKey,
			String status, Integer weeklyPeriods) {

		this.sectionId = sectionId;
		this.className = className;
		this.sectionName = sectionName;
		this.curriculumKey = curriculumKey;
		this.status = status;
		this.weeklyPeriods = weeklyPeriods;
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

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getCurriculumKey() {
		return curriculumKey;
	}

	public void setCurriculumKey(String curriculumKey) {
		this.curriculumKey = curriculumKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getWeeklyPeriods() {
		return weeklyPeriods;
	}

	public void setWeeklyPeriods(Integer weeklyPeriods) {
		this.weeklyPeriods = weeklyPeriods;
	}
}
