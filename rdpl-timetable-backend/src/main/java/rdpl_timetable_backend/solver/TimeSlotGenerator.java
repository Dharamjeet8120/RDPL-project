
package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.BellSchedule;
import rdpl_timetable_backend.model.DailySchedule;
import rdpl_timetable_backend.model.TimePeriod;
import rdpl_timetable_backend.model.TimeSlot;

@Component
public class TimeSlotGenerator {

	public List<TimeSlot> generate(BellSchedule bellSchedule) {

		List<TimeSlot> timeSlots = new ArrayList<>();

		if (bellSchedule == null || bellSchedule.getDailySchedules() == null) {
			throw new IllegalArgumentException("Bell schedule is missing.");
		}

		for (DailySchedule dailySchedule : bellSchedule.getDailySchedules()) {

			for (String day : dailySchedule.getDays()) {

				for (TimePeriod period : dailySchedule.getTeachingPeriods()) {

					TimeSlot slot = new TimeSlot(day, period.getPeriodNumber(), period.getLabel(),
							period.getStartTime(), period.getEndTime());

					timeSlots.add(slot);
				}
			}
		}

		if (timeSlots.size() != bellSchedule.getWeeklyTeachingCapacity()) {

			throw new IllegalStateException("Generated slot count is " + timeSlots.size() + " but expected "
					+ bellSchedule.getWeeklyTeachingCapacity());
		}

		return timeSlots;
	}
}
