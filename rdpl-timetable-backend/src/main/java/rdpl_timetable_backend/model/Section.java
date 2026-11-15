package rdpl_timetable_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "master_sections")
public class Section {
	@Id
	@Column(length = 80, nullable = false)
	private String id;
	@Column(nullable = false, length = 80)
	private String className;
	@Column(nullable = false, length = 80)
	private String sectionName;
	private int grade;
	private int capacity;
	@Column(length = 100)
	private String curriculumKey;

	public Section() {
	}

	public Section(String id, String className, String sectionName, int grade, int capacity, String curriculumKey) {
		this.id = id;
		this.className = className;
		this.sectionName = sectionName;
		this.grade = grade;
		this.capacity = capacity;
		this.curriculumKey = curriculumKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String v) {
		id = v;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String v) {
		className = v;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String v) {
		sectionName = v;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int v) {
		grade = v;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int v) {
		capacity = v;
	}

	public String getCurriculumKey() {
		return curriculumKey;
	}

	public void setCurriculumKey(String v) {
		curriculumKey = v;
	}
}
