package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "master_teachers")
public class Teacher {
	@Id
	@Column(name = "teacher_id", length = 40, nullable = false)
	private String teacherId;
	@Column(nullable = false, length = 180)
	private String teacherName;
	@OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<TeacherAssignment> assignments = new ArrayList<>();
	private int totalPeriodsPerWeek;
	private int maxPeriodsPerDay;
	private int maxPeriodsPerWeek;

	public Teacher() {
	}

	public Teacher(String teacherId, String teacherName, List<TeacherAssignment> assignments, int totalPeriodsPerWeek,
			int maxPeriodsPerDay, int maxPeriodsPerWeek) {
		this.teacherId = teacherId;
		this.teacherName = teacherName;
		setAssignments(assignments);
		this.totalPeriodsPerWeek = totalPeriodsPerWeek;
		this.maxPeriodsPerDay = maxPeriodsPerDay;
		this.maxPeriodsPerWeek = maxPeriodsPerWeek;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String v) {
		teacherId = v;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String v) {
		teacherName = v;
	}

	public List<TeacherAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(List<TeacherAssignment> rows) {
		this.assignments = new ArrayList<>();
		if (rows != null)
			for (TeacherAssignment a : rows)
				addAssignment(a);
	}

	public void addAssignment(TeacherAssignment a) {
		if (a != null) {
			a.setTeacher(this);
			assignments.add(a);
		}
	}

	public int getTotalPeriodsPerWeek() {
		return totalPeriodsPerWeek;
	}

	public void setTotalPeriodsPerWeek(int v) {
		totalPeriodsPerWeek = v;
	}

	public int getMaxPeriodsPerDay() {
		return maxPeriodsPerDay;
	}

	public void setMaxPeriodsPerDay(int v) {
		maxPeriodsPerDay = v;
	}

	public int getMaxPeriodsPerWeek() {
		return maxPeriodsPerWeek;
	}

	public void setMaxPeriodsPerWeek(int v) {
		maxPeriodsPerWeek = v;
	}
}
