package rdpl_timetable_backend.loader;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.SchoolSample;
import tools.jackson.databind.ObjectMapper;

@Component
public class SchoolSampleLoader {

	private final ObjectMapper objectMapper;

	public SchoolSampleLoader(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public SchoolSample loadSchoolSample() {

		try {
			ClassPathResource resource = new ClassPathResource("data/school-sample.json");

			try (InputStream inputStream = resource.getInputStream()) {

				SchoolSample schoolSample = objectMapper.readValue(inputStream, SchoolSample.class);

				if (!schoolSample.isSampleMode()) {
					throw new IllegalStateException("school-sample.json is not in sample mode");
				}

				return schoolSample;
			}

		} catch (IOException e) {

			throw new RuntimeException("Unable to load school-sample.json: " + e.getMessage(), e);
		}
	}
}