
package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;

public class SectionTimetable {

	private String sectionId;

	private String className;

	private String sectionName;

	private List<TimetableSlot> slots = new ArrayList<>();

	public SectionTimetable() {
	}

	public SectionTimetable(String sectionId, String className, String sectionName, List<TimetableSlot> slots) {

		this.sectionId = sectionId;
		this.className = className;
		this.sectionName = sectionName;
		this.slots = slots;
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

	public List<TimetableSlot> getSlots() {
		return slots;
	}

	public void setSlots(List<TimetableSlot> slots) {
		this.slots = slots;
	}
}
