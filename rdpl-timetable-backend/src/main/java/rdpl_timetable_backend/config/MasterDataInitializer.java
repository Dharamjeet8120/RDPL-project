package rdpl_timetable_backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import rdpl_timetable_backend.service.MasterDataService;

@Component
public class MasterDataInitializer implements ApplicationRunner {
	private final MasterDataService service;

	public MasterDataInitializer(MasterDataService service) {
		this.service = service;
	}

	@Override
	public void run(ApplicationArguments args) {
		service.importJsonIfEmpty();
	}
}
