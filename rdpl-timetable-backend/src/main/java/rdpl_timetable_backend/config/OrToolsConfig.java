
package rdpl_timetable_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.ortools.Loader;

@Configuration
public class OrToolsConfig {

	private static boolean initialized = false;

	@Bean
	public String initializeOrTools() {

		if (!initialized) {

			Loader.loadNativeLibraries();

			initialized = true;

			System.out.println("Google OR-Tools native libraries loaded successfully!");
		}

		return "OR-Tools Initialized";
	}
}
