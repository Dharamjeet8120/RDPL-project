package rdpl_timetable_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.service.TimetableGenerationService;
import rdpl_timetable_backend.solver.SolverResult;

@RestController
@RequestMapping("/api/timetable")
public class TimetableGenerationController {

	private final TimetableGenerationService timetableGenerationService;

	public TimetableGenerationController(TimetableGenerationService timetableGenerationService) {
		this.timetableGenerationService = timetableGenerationService;
	}

	@PostMapping("/generate")
	public SolverResult generateTimetable() {
		return timetableGenerationService.generateTimetable();
	}
}