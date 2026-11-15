
package rdpl_timetable_backend.model;

public class TimetableEntry {

	private String sectionId;
	private String subjectName;
	private String teacherId;
	private String type;
	private String day;
	private int periodNumber;
	private String periodLabel;
	private String startTime;
	private String endTime;

	public TimetableEntry() {
	}

	public TimetableEntry(String sectionId, String subjectName, String teacherId, String type, TimeSlot timeSlot) {

		this.sectionId = sectionId;
		this.subjectName = subjectName;
		this.teacherId = teacherId;
		this.type = type;

		this.day = timeSlot.getDay();
		this.periodNumber = timeSlot.getPeriodNumber();
		this.periodLabel = timeSlot.getPeriodLabel();
		this.startTime = timeSlot.getStartTime();
		this.endTime = timeSlot.getEndTime();
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(int periodNumber) {
		this.periodNumber = periodNumber;
	}

	public String getPeriodLabel() {
		return periodLabel;
	}

	public void setPeriodLabel(String periodLabel) {
		this.periodLabel = periodLabel;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
