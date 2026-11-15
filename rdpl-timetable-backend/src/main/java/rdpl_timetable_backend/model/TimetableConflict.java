
package rdpl_timetable_backend.model;

public class TimetableConflict {

	private String conflictId;

	private String conflictType;

	private String severity;

	private String description;

	private String sectionId;

	private String teacherId;

	private String day;

	private Integer periodNumber;

	public TimetableConflict() {
	}

	public String getConflictId() {
		return conflictId;
	}

	public void setConflictId(String conflictId) {
		this.conflictId = conflictId;
	}

	public String getConflictType() {
		return conflictType;
	}

	public void setConflictType(String conflictType) {
		this.conflictType = conflictType;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Integer getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(Integer periodNumber) {
		this.periodNumber = periodNumber;
	}
}
