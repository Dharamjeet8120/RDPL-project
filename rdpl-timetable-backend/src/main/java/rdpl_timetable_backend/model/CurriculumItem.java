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
@Table(name = "master_curriculum_items")
public class CurriculumItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_curriculum_id", nullable = false)
	private ClassCurriculum classCurriculum;
	@Column(length = 30)
	private String type;
	@Column(nullable = false, length = 180)
	private String name;
	private int periodsPerDay;
	private int periodsPerWeek;

	public CurriculumItem() {
	}

	public CurriculumItem(String type, String name, int periodsPerDay, int periodsPerWeek) {
		this.type = type;
		this.name = name;
		this.periodsPerDay = periodsPerDay;
		this.periodsPerWeek = periodsPerWeek;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long v) {
		id = v;
	}

	@JsonIgnore
	public ClassCurriculum getClassCurriculum() {
		return classCurriculum;
	}

	public void setClassCurriculum(ClassCurriculum v) {
		classCurriculum = v;
	}

	public String getType() {
		return type;
	}

	public void setType(String v) {
		type = v;
	}

	public String getName() {
		return name;
	}

	public void setName(String v) {
		name = v;
	}

	public int getPeriodsPerDay() {
		return periodsPerDay;
	}

	public void setPeriodsPerDay(int v) {
		periodsPerDay = v;
	}

	public int getPeriodsPerWeek() {
		return periodsPerWeek;
	}

	public void setPeriodsPerWeek(int v) {
		periodsPerWeek = v;
	}
}
