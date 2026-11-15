
package rdpl_timetable_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rdpl_timetable_backend.model.GeneratedTimetable;
import rdpl_timetable_backend.repository.GeneratedTimetableRepository;

@Service
@Transactional
public class GeneratedTimetableService {

	private final GeneratedTimetableRepository repository;

	public GeneratedTimetableService(GeneratedTimetableRepository repository) {
		this.repository = repository;
	}

	// Save a new timetable generation.
	public GeneratedTimetable save(String academicYear, String status) {

		if (academicYear == null || academicYear.isBlank()) {
			throw new IllegalArgumentException("Academic year is required.");
		}

		if (status == null || status.isBlank()) {
			throw new IllegalArgumentException("Timetable status is required.");
		}

		GeneratedTimetable timetable = new GeneratedTimetable();

		timetable.setAcademicYear(academicYear);
		timetable.setStatus(status);

		return repository.save(timetable);
	}

	// Get all timetable generation history.
	@Transactional(readOnly = true)
	public List<GeneratedTimetable> getAllHistory() {
		return repository.findAllByOrderByGeneratedAtDesc();
	}

	// Get timetable history by academic year.
	@Transactional(readOnly = true)
	public List<GeneratedTimetable> getHistoryByAcademicYear(String academicYear) {

		return repository.findByAcademicYearOrderByGeneratedAtDesc(academicYear);
	}

	// Get a generation by ID.
	@Transactional(readOnly = true)
	public GeneratedTimetable getById(Long id) {

		return repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Timetable generation not found: " + id));
	}

	// Delete a generation.
	public void delete(Long id) {

		if (!repository.existsById(id)) {
			throw new IllegalArgumentException("Timetable generation not found: " + id);
		}

		repository.deleteById(id);
	}
}
