package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "master_curriculum")
public class CurriculumData {
	@Id
	@Column(name = "academic_year", length = 20)
	private String academicYear;
	private int weeklyTeachingCapacity;
	@OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<ClassCurriculum> classes = new ArrayList<>();

	public CurriculumData() {
	}

	public CurriculumData(String academicYear, int weeklyTeachingCapacity, List<ClassCurriculum> classes) {
		this.academicYear = academicYear;
		this.weeklyTeachingCapacity = weeklyTeachingCapacity;
		setClasses(classes);
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

	public List<ClassCurriculum> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassCurriculum> rows) {
		classes = new ArrayList<>();
		if (rows != null)
			for (ClassCurriculum c : rows)
				addClass(c);
	}

	public void addClass(ClassCurriculum c) {
		if (c != null) {
			c.setCurriculum(this);
			classes.add(c);
		}
	}
}
