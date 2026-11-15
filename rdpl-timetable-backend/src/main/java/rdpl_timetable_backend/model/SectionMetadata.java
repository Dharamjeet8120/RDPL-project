package rdpl_timetable_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "master_section_metadata")
public class SectionMetadata {
	@Id
	private String id = "default";
	private String academicYear;
	private int weeklyTeachingCapacity;

	public SectionMetadata() {
	}

	public SectionMetadata(String academicYear, int weeklyTeachingCapacity) {
		this.id = "default";
		this.academicYear = academicYear;
		this.weeklyTeachingCapacity = weeklyTeachingCapacity;
	}

	public String getId() {
		return id;
	}

	public void setId(String v) {
		id = v;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String v) {
		academicYear = v;
	}

	public int getWeeklyTeachingCapacity() {
		return weeklyTeachingCapacity;
	}

	public void setWeeklyTeachingCapacity(int v) {
		weeklyTeachingCapacity = v;
	}
}
