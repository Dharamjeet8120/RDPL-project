package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "master_curriculum_classes", uniqueConstraints = @UniqueConstraint(columnNames = { "academic_year",
		"class_name" }))
public class ClassCurriculum {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "academic_year", nullable = false)
	private CurriculumData curriculum;
	@Column(name = "class_name", nullable = false, length = 80)
	private String className;
	private int totalPeriodsPerWeek;
	private int subjectPeriodsPerWeek;
	private int activityPeriodsPerWeek;
	@OneToMany(mappedBy = "classCurriculum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<CurriculumItem> items = new ArrayList<>();

	public ClassCurriculum() {
	}

	public ClassCurriculum(String className, int totalPeriodsPerWeek, int subjectPeriodsPerWeek,
			int activityPeriodsPerWeek, List<CurriculumItem> items) {
		this.className = className;
		this.totalPeriodsPerWeek = totalPeriodsPerWeek;
		this.subjectPeriodsPerWeek = subjectPeriodsPerWeek;
		this.activityPeriodsPerWeek = activityPeriodsPerWeek;
		setItems(items);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long v) {
		id = v;
	}

	@JsonIgnore
	public CurriculumData getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(CurriculumData v) {
		curriculum = v;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String v) {
		className = v;
	}

	public int getTotalPeriodsPerWeek() {
		return totalPeriodsPerWeek;
	}

	public void setTotalPeriodsPerWeek(int v) {
		totalPeriodsPerWeek = v;
	}

	public int getSubjectPeriodsPerWeek() {
		return subjectPeriodsPerWeek;
	}

	public void setSubjectPeriodsPerWeek(int v) {
		subjectPeriodsPerWeek = v;
	}

	public int getActivityPeriodsPerWeek() {
		return activityPeriodsPerWeek;
	}

	public void setActivityPeriodsPerWeek(int v) {
		activityPeriodsPerWeek = v;
	}

	public List<CurriculumItem> getItems() {
		return items;
	}

	public void setItems(List<CurriculumItem> rows) {
		items = new ArrayList<>();
		if (rows != null)
			for (CurriculumItem i : rows)
				addItem(i);
	}

	public void addItem(CurriculumItem i) {
		if (i != null) {
			i.setClassCurriculum(this);
			items.add(i);
		}
	}
}
