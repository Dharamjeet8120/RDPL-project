package rdpl_timetable_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.model.ClassCurriculum;
import rdpl_timetable_backend.service.MasterDataService;

@RestController
@RequestMapping("/api/timetable")
public class CurriculumController {
	private final MasterDataService service;

	public CurriculumController(MasterDataService service) {
		this.service = service;
	}

	@GetMapping("/curriculum")
	public List<ClassCurriculum> getAllCurriculum() {
		return service.getCurriculumData().getClasses();
	}

	@GetMapping("/curriculum/count")
	public int getClassCount() {
		return service.getCurriculumData().getClasses().size();
	}

	@GetMapping("/curriculum/academic-year")
	public String getAcademicYear() {
		return service.getCurriculumData().getAcademicYear();
	}

	@GetMapping("/curriculum/{className}")
	public ClassCurriculum getCurriculum(@PathVariable String className) {
		return service.getCurriculumClass(className);
	}

	@PutMapping("/curriculum/{className}")
	public ClassCurriculum saveCurriculum(@PathVariable String className, @RequestBody ClassCurriculum curriculum) {
		return service.saveCurriculumClass(className, curriculum);
	}

	@DeleteMapping("/curriculum/{className}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCurriculum(@PathVariable String className) {
		service.deleteCurriculumClass(className);
	}
}
