package rdpl_timetable_backend.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rdpl_timetable_backend.model.TimeSlot;

/**
 * Weekly time slots as per data/bell-schedule.json: Monday-Thursday 8 periods,
 * Friday-Saturday 10 periods = 52 slots.
 */
@Configuration
public class TimeSlotConfig {

	@Bean
	public List<TimeSlot> timeSlots() {

		List<TimeSlot> slots = new ArrayList<>();

		String[] days8 = { "Monday", "Tuesday", "Wednesday", "Thursday" };
		String[] start8 = { "07:45", "08:25", "09:15", "09:55", "11:05", "11:40", "12:20", "12:55" };
		String[] end8 = { "08:25", "09:05", "09:55", "10:35", "11:40", "12:15", "12:55", "13:30" };
		for (String day : days8) {
			for (int i = 0; i < 8; i++) {
				slots.add(new TimeSlot(day, i + 1, "Period " + (i + 1), start8[i], end8[i]));
			}
		}

		String[] days10 = { "Friday", "Saturday" };
		String[] start10 = { "07:45", "08:15", "08:55", "09:30", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00" };
		String[] end10 = { "08:15", "08:45", "09:30", "10:00", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30" };
		for (String day : days10) {
			for (int i = 0; i < 10; i++) {
				slots.add(new TimeSlot(day, i + 1, "Period " + (i + 1), start10[i], end10[i]));
			}
		}

		return slots;
	}
}
