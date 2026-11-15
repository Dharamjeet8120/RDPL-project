
package rdpl_timetable_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rdpl_timetable_backend.model.GeneratedTimetable;
import rdpl_timetable_backend.service.GeneratedTimetableService;

@RestController
@RequestMapping("/api/timetable/history")
@CrossOrigin(origins = "http://localhost:5173")
public class GeneratedTimetableController {

	private final GeneratedTimetableService service;

	public GeneratedTimetableController(GeneratedTimetableService service) {
		this.service = service;
	}

	// Save a timetable generation.
	@PostMapping
	public ResponseEntity<GeneratedTimetable> save(@RequestParam String academicYear, @RequestParam String status) {

		GeneratedTimetable saved = service.save(academicYear, status);

		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	// Get all timetable history.
	@GetMapping
	public ResponseEntity<List<GeneratedTimetable>> getAllHistory() {

		return ResponseEntity.ok(service.getAllHistory());
	}

	// Get history by academic year.
	@GetMapping("/academic-year/{academicYear}")
	public ResponseEntity<List<GeneratedTimetable>> getByAcademicYear(@PathVariable String academicYear) {

		return ResponseEntity.ok(service.getHistoryByAcademicYear(academicYear));
	}

	// Get history by ID.
	@GetMapping("/{id}")
	public ResponseEntity<GeneratedTimetable> getById(@PathVariable Long id) {

		return ResponseEntity.ok(service.getById(id));
	}

	// Delete history by ID.
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		service.delete(id);

		return ResponseEntity.noContent().build();
	}
}
