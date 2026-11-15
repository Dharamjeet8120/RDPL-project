
package rdpl_timetable_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.CurriculumDataLoader;
import rdpl_timetable_backend.loader.JsonDataLoader;
import rdpl_timetable_backend.loader.TeachersDataLoader;
import rdpl_timetable_backend.model.DataValidationResult;
import rdpl_timetable_backend.validator.DataValidator;

@RestController
@RequestMapping("/api/timetable")
public class ValidationController {

	private final JsonDataLoader jsonDataLoader;
	private final TeachersDataLoader teachersDataLoader;
	private final CurriculumDataLoader curriculumDataLoader;
	private final DataValidator dataValidator;

	public ValidationController(JsonDataLoader jsonDataLoader, TeachersDataLoader teachersDataLoader,
			CurriculumDataLoader curriculumDataLoader, DataValidator dataValidator) {

		this.jsonDataLoader = jsonDataLoader;
		this.teachersDataLoader = teachersDataLoader;
		this.curriculumDataLoader = curriculumDataLoader;
		this.dataValidator = dataValidator;
	}

	@GetMapping("/validate")
	public DataValidationResult validateAllData() {

		return dataValidator.validate(jsonDataLoader.loadSections(), teachersDataLoader.loadTeachers(),
				curriculumDataLoader.loadCurriculum());
	}
}
