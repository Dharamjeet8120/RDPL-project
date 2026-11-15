package rdpl_timetable_backend.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.JsonDataLoader;
import rdpl_timetable_backend.loader.TeachersDataLoader;
import rdpl_timetable_backend.model.GeneratedTimetable;
import rdpl_timetable_backend.model.GeneratedTimetableEntry;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.service.GeneratedTimetableEntryService;
import rdpl_timetable_backend.service.GeneratedTimetableService;

/**
 * Plain-text view of a generated timetable.
 *
 * GET /api/timetable/text/latest?view=section|teacher&id=...
 * GET /api/timetable/text/{timetableId}?view=section|teacher&id=...
 *
 * view=section (default): one weekly grid per section (id = sectionId or section name, optional)
 * view=teacher          : one weekly grid per teacher (id = teacherId or teacher name, optional)
 */
@RestController
@RequestMapping("/api/timetable/text")
@CrossOrigin(origins = "http://localhost:5173")
public class TimetableTextController {

	private static final String[] DAYS = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };

	private final GeneratedTimetableService timetableService;
	private final GeneratedTimetableEntryService entryService;
	private final JsonDataLoader jsonDataLoader;
	private final TeachersDataLoader teachersDataLoader;

	public TimetableTextController(GeneratedTimetableService timetableService,
			GeneratedTimetableEntryService entryService, JsonDataLoader jsonDataLoader,
			TeachersDataLoader teachersDataLoader) {
		this.timetableService = timetableService;
		this.entryService = entryService;
		this.jsonDataLoader = jsonDataLoader;
		this.teachersDataLoader = teachersDataLoader;
	}

	@GetMapping(value = "/latest", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
	public ResponseEntity<String> latest(@RequestParam(defaultValue = "section") String view,
			@RequestParam(required = false) String id) {

		List<GeneratedTimetable> history = timetableService.getAllHistory();

		if (history == null || history.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No generated timetable found.");
		}

		return render(history.get(0).getId(), view, id);
	}

	@GetMapping(value = "/{timetableId}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
	public ResponseEntity<String> byId(@PathVariable Long timetableId, @RequestParam(defaultValue = "section") String view,
			@RequestParam(required = false) String id) {

		return render(timetableId, view, id);
	}

	private ResponseEntity<String> render(Long timetableId, String view, String id) {

		List<GeneratedTimetableEntry> entries = entryService.getEntries(timetableId);

		if (entries == null || entries.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No entries found for timetable " + timetableId + ".");
		}

		Map<String, String> teacherNames = new HashMap<>();
		for (Teacher teacher : teachersDataLoader.loadTeachers().getTeachers()) {
			teacherNames.put(teacher.getTeacherId(), teacher.getTeacherName());
		}

		Map<String, String> sectionNames = new LinkedHashMap<>();
		for (Section section : jsonDataLoader.loadSections().getSections()) {
			sectionNames.put(section.getId(), section.getClassName() + " - " + section.getSectionName());
		}

		boolean teacherView = "teacher".equalsIgnoreCase(view);

		// group key -> entries
		Map<String, List<GeneratedTimetableEntry>> groups = new LinkedHashMap<>();

		List<GeneratedTimetableEntry> sorted = new ArrayList<>(entries);
		sorted.sort(Comparator.comparing((GeneratedTimetableEntry e) -> String.valueOf(teacherView ? e.getTeacherId() : e.getSectionId())));

		for (GeneratedTimetableEntry entry : sorted) {
			String key = teacherView ? entry.getTeacherId() : entry.getSectionId();
			if (key == null) {
				continue;
			}
			if (id != null) {
				String name = teacherView ? teacherNames.get(key) : sectionNames.get(key);
				boolean match = id.equalsIgnoreCase(key) || (name != null && (name.equalsIgnoreCase(id)
						|| (id.length() > 2 && name.toLowerCase(Locale.ROOT).contains(id.toLowerCase(Locale.ROOT)))));
				if (!match) {
					continue;
				}
			}
			groups.computeIfAbsent(key, k -> new ArrayList<>()).add(entry);
		}

		if (groups.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No timetable found for id: " + id);
		}

		StringBuilder out = new StringBuilder();
		out.append("TIMETABLE #").append(timetableId).append(" (").append(teacherView ? "teacher" : "section")
				.append(" view)\n\n");

		for (Map.Entry<String, List<GeneratedTimetableEntry>> group : groups.entrySet()) {

			String title = teacherView ? group.getKey() + " - " + teacherNames.getOrDefault(group.getKey(), "?")
					: group.getKey() + " - " + sectionNames.getOrDefault(group.getKey(), "?");

			out.append(grid(title, group.getValue(), teacherView, teacherNames, sectionNames)).append("\n");
		}

		return ResponseEntity.ok(out.toString());
	}

	private String grid(String title, List<GeneratedTimetableEntry> list, boolean teacherView,
			Map<String, String> teacherNames, Map<String, String> sectionNames) {

		int maxPeriod = 0;
		Map<String, String> cells = new HashMap<>();

		for (GeneratedTimetableEntry e : list) {
			if (e.getDay() == null || e.getPeriodNumber() == null) {
				continue;
			}
			maxPeriod = Math.max(maxPeriod, e.getPeriodNumber());

			String detail = teacherView ? sectionNames.getOrDefault(e.getSectionId(), e.getSectionId())
					: teacherNames.getOrDefault(e.getTeacherId(), e.getTeacherId());

			cells.put(e.getDay().toUpperCase(Locale.ROOT) + "|" + e.getPeriodNumber(), e.getSubjectName() + " (" + detail + ")");
		}

		int width = 8;
		for (String cell : cells.values()) {
			width = Math.max(width, cell.length());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("=== ").append(title).append(" ===\n");

		sb.append(pad("Period", 7));
		for (String day : DAYS) {
			sb.append(" | ").append(pad(day.substring(0, 1) + day.substring(1, 3).toLowerCase(Locale.ROOT), width));
		}
		sb.append("\n");

		for (int p = 1; p <= maxPeriod; p++) {
			sb.append(pad(String.valueOf(p), 7));
			for (String day : DAYS) {
				sb.append(" | ").append(pad(cells.getOrDefault(day + "|" + p, "-"), width));
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	private String pad(String value, int width) {
		StringBuilder sb = new StringBuilder(value);
		while (sb.length() < width) {
			sb.append(' ');
		}
		return sb.toString();
	}
}
