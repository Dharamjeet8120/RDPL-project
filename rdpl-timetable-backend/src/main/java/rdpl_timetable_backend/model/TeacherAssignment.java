package rdpl_timetable_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "master_teacher_assignments")
public class TeacherAssignment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id", nullable = false)
	@JsonIgnore
	private Teacher teacher;
	@Column(length = 80)
	private String className;
	@Column(length = 80)
	private String sectionId;
	@Column(length = 80)
	private String sectionName;
	@Column(length = 30)
	private String type;
	@Column(length = 180)
	private String subjectOrActivity;
	private int periodsPerWeek;

	public TeacherAssignment() {
	}

	public TeacherAssignment(String className, String sectionId, String sectionName, String type,
			String subjectOrActivity, int periodsPerWeek) {
		this.className = className;
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.type = type;
		this.subjectOrActivity = subjectOrActivity;
		this.periodsPerWeek = periodsPerWeek;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long v) {
		id = v;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher v) {
		teacher = v;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String v) {
		className = v;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String v) {
		sectionId = v;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String v) {
		sectionName = v;
	}

	public String getType() {
		return type;
	}

	public void setType(String v) {
		type = v;
	}

	public String getSubjectOrActivity() {
		return subjectOrActivity;
	}

	public void setSubjectOrActivity(String v) {
		subjectOrActivity = v;
	}

	public int getPeriodsPerWeek() {
		return periodsPerWeek;
	}

	public void setPeriodsPerWeek(int v) {
		periodsPerWeek = v;
	}
}
