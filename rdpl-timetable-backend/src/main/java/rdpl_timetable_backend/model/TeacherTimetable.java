
package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;

public class TeacherTimetable {

	private String teacherId;

	private String teacherName;

	private List<TimetableSlot> slots = new ArrayList<>();

	public TeacherTimetable() {
	}

	public TeacherTimetable(String teacherId, String teacherName, List<TimetableSlot> slots) {

		this.teacherId = teacherId;
		this.teacherName = teacherName;
		this.slots = slots;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public List<TimetableSlot> getSlots() {
		return slots;
	}

	public void setSlots(List<TimetableSlot> slots) {
		this.slots = slots;
	}
}
