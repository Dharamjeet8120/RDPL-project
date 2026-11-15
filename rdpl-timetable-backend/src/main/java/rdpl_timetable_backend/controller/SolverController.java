
package rdpl_timetable_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.BellScheduleLoader;
import rdpl_timetable_backend.model.BellSchedule;
import rdpl_timetable_backend.model.TimeSlot;
import rdpl_timetable_backend.solver.SolverResult;
import rdpl_timetable_backend.solver.TimeSlotGenerator;
import rdpl_timetable_backend.solver.TimetableSolver;

@RestController
@RequestMapping("/api/timetable")
public class SolverController {

	private final BellScheduleLoader bellScheduleLoader;

	private final TimeSlotGenerator timeSlotGenerator;

	private final TimetableSolver timetableSolver;

	public SolverController(BellScheduleLoader bellScheduleLoader, TimeSlotGenerator timeSlotGenerator,
			TimetableSolver timetableSolver) {

		this.bellScheduleLoader = bellScheduleLoader;
		this.timeSlotGenerator = timeSlotGenerator;
		this.timetableSolver = timetableSolver;
	}

	@GetMapping("/solver/test")
	public SolverResult testSolver(@RequestParam(defaultValue = "TEST-SECTION") String sectionId,
			@RequestParam(defaultValue = "English") String subjectName,
			@RequestParam(defaultValue = "TEST-TEACHER") String teacherId) {

		BellSchedule bellSchedule = bellScheduleLoader.loadBellSchedule();

		List<TimeSlot> timeSlots = timeSlotGenerator.generate(bellSchedule);

		return timetableSolver.solve(sectionId, subjectName, teacherId, "SUBJECT", timeSlots);
	}
}
