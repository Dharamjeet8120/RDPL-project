
package rdpl_timetable_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "generated_timetable_entries")
public class GeneratedTimetableEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "timetable_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private GeneratedTimetable generatedTimetable;

	@Column(name = "section_id")
	private String sectionId;

	@Column(name = "subject_name")
	private String subjectName;

	@Column(name = "teacher_id")
	private String teacherId;

	@Column(name = "type")
	private String type;

	@Column(name = "day")
	private String day;

	@Column(name = "period_number")
	private Integer periodNumber;

	public GeneratedTimetableEntry() {
	}

	public Long getId() {
		return id;
	}

	public GeneratedTimetable getGeneratedTimetable() {
		return generatedTimetable;
	}

	public void setGeneratedTimetable(GeneratedTimetable generatedTimetable) {
		this.generatedTimetable = generatedTimetable;
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

	public Integer getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(Integer periodNumber) {
		this.periodNumber = periodNumber;
	}
}
