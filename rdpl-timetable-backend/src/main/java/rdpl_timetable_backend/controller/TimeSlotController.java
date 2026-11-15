
package rdpl_timetable_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.loader.BellScheduleLoader;
import rdpl_timetable_backend.model.BellSchedule;
import rdpl_timetable_backend.model.TimeSlot;
import rdpl_timetable_backend.solver.TimeSlotGenerator;

@RestController
@RequestMapping("/api/timetable")
public class TimeSlotController {

	private final BellScheduleLoader bellScheduleLoader;

	private final TimeSlotGenerator timeSlotGenerator;

	public TimeSlotController(BellScheduleLoader bellScheduleLoader, TimeSlotGenerator timeSlotGenerator) {

		this.bellScheduleLoader = bellScheduleLoader;
		this.timeSlotGenerator = timeSlotGenerator;
	}

	@GetMapping("/time-slots")
	public List<TimeSlot> getTimeSlots() {

		BellSchedule bellSchedule = bellScheduleLoader.loadBellSchedule();

		return timeSlotGenerator.generate(bellSchedule);
	}

	@GetMapping("/time-slots/count")
	public int getTimeSlotCount() {

		BellSchedule bellSchedule = bellScheduleLoader.loadBellSchedule();

		return timeSlotGenerator.generate(bellSchedule).size();
	}
}
