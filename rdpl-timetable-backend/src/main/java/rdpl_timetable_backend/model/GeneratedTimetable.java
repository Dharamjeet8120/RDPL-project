
package rdpl_timetable_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity
@Table(name = "generated_timetables")
public class GeneratedTimetable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String academicYear;

	@Column(nullable = false)
	private String status;

	@Column(nullable = false)
	private LocalDateTime generatedAt;

	public GeneratedTimetable() {
	}

	@PrePersist
	public void onCreate() {
		generatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getGeneratedAt() {
		return generatedAt;
	}
}
