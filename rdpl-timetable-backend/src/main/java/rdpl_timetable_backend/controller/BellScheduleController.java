package rdpl_timetable_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.BellScheduleLoader;
import rdpl_timetable_backend.model.BellSchedule;

@RestController
@RequestMapping("/api/timetable")
public class BellScheduleController {

	private final BellScheduleLoader bellScheduleLoader;

	public BellScheduleController(BellScheduleLoader bellScheduleLoader) {
		this.bellScheduleLoader = bellScheduleLoader;
	}

	@GetMapping("/bell-schedule")
	public BellSchedule getBellSchedule() {

		return bellScheduleLoader.loadBellSchedule();
	}

	@GetMapping("/bell-schedule/capacity")
	public int getWeeklyCapacity() {

		return bellScheduleLoader.loadBellSchedule().getWeeklyTeachingCapacity();
	}
}