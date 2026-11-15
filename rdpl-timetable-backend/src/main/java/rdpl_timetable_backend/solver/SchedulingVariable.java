
package rdpl_timetable_backend.solver;

import com.google.ortools.sat.IntVar;

import rdpl_timetable_backend.model.TimeSlot;

public class SchedulingVariable {

	private String sectionId;

	private String subjectName;

	private String teacherId;

	private String type;

	private TimeSlot timeSlot;

	private IntVar variable;

	public SchedulingVariable() {
	}

	public SchedulingVariable(String sectionId, String subjectName, String teacherId, String type, TimeSlot timeSlot,
			IntVar variable) {

		this.sectionId = sectionId;
		this.subjectName = subjectName;
		this.teacherId = teacherId;
		this.type = type;
		this.timeSlot = timeSlot;
		this.variable = variable;
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

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}

	public IntVar getVariable() {
		return variable;
	}

	public void setVariable(IntVar variable) {
		this.variable = variable;
	}
}
