
package rdpl_timetable_backend.loader;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.BellSchedule;
import tools.jackson.databind.ObjectMapper;

@Component
public class BellScheduleLoader {

	private final ObjectMapper objectMapper;

	public BellScheduleLoader(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public BellSchedule loadBellSchedule() {

		try {
			ClassPathResource resource = new ClassPathResource("data/bell-schedule.json");

			try (InputStream inputStream = resource.getInputStream()) {

				BellSchedule bellSchedule = objectMapper.readValue(inputStream, BellSchedule.class);

				if (bellSchedule.getDailySchedules() == null || bellSchedule.getDailySchedules().isEmpty()) {

					throw new IllegalStateException("Daily schedules are missing in bell-schedule.json");
				}

				return bellSchedule;
			}

		} catch (IOException e) {

			throw new RuntimeException("Unable to load bell-schedule.json: " + e.getMessage(), e);
		}
	}
}
