package rdpl_timetable_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.service.MasterDataService;

@RestController
@RequestMapping("/api/timetable")
public class SectionController {
	private final MasterDataService service;

	public SectionController(MasterDataService service) {
		this.service = service;
	}

	@GetMapping("/sections")
	public List<Section> getAllSections() {
		return service.getSectionsData().getSections();
	}

	@GetMapping("/sections/count")
	public int getSectionCount() {
		return service.getSectionsData().getSections().size();
	}

	@GetMapping("/sections/academic-year")
	public String getAcademicYear() {
		return service.getSectionsData().getAcademicYear();
	}

	@GetMapping("/sections/{id}")
	public Section getSection(@PathVariable String id) {
		return service.getSection(id);
	}

	@PostMapping("/sections")
	@ResponseStatus(HttpStatus.CREATED)
	public Section createSection(@RequestBody Section section) {
		return service.createSection(section);
	}

	@PutMapping("/sections/{id}")
	public Section updateSection(@PathVariable String id, @RequestBody Section section) {
		return service.updateSection(id, section);
	}

	@DeleteMapping("/sections/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSection(@PathVariable String id) {
		service.deleteSection(id);
	}
}
