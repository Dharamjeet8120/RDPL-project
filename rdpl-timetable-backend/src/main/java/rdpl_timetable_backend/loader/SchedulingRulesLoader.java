package rdpl_timetable_backend.loader;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.SchedulingRules;
import tools.jackson.databind.ObjectMapper;

@Component
public class SchedulingRulesLoader {

	private final ObjectMapper objectMapper;

	public SchedulingRulesLoader(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public SchedulingRules loadSchedulingRules() {

		try {
			ClassPathResource resource = new ClassPathResource("data/scheduling-rules.json");

			try (InputStream inputStream = resource.getInputStream()) {

				SchedulingRules rules = objectMapper.readValue(inputStream, SchedulingRules.class);

				if (rules.getMandatoryHard() == null) {
					throw new IllegalStateException("Mandatory hard rules are missing in scheduling-rules.json");
				}

				return rules;
			}

		} catch (IOException e) {

			throw new RuntimeException("Unable to load scheduling-rules.json: " + e.getMessage(), e);
		}
	}
}