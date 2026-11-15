
package rdpl_timetable_backend.controller;

import rdpl_timetable_backend.model.GeneratedTimetableEntry;
import rdpl_timetable_backend.service.GeneratedTimetableEntryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable/history/{timetableId}/entries")
@CrossOrigin(origins = "http://localhost:5173")
public class GeneratedTimetableEntryController {

	private final GeneratedTimetableEntryService entryService;

	public GeneratedTimetableEntryController(GeneratedTimetableEntryService entryService) {
		this.entryService = entryService;
	}

	@PostMapping
	public ResponseEntity<List<GeneratedTimetableEntry>> saveEntries(@PathVariable Long timetableId,
			@RequestBody List<GeneratedTimetableEntry> entries) {

		return ResponseEntity.ok(entryService.saveEntries(timetableId, entries));
	}

	@GetMapping
	public ResponseEntity<List<GeneratedTimetableEntry>> getEntries(@PathVariable Long timetableId) {

		return ResponseEntity.ok(entryService.getEntries(timetableId));
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countEntries(@PathVariable Long timetableId) {

		return ResponseEntity.ok(entryService.countEntries(timetableId));
	}
}
