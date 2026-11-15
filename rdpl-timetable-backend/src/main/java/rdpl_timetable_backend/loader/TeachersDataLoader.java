package rdpl_timetable_backend.loader;

import org.springframework.stereotype.Component;
import rdpl_timetable_backend.model.TeachersData;
import rdpl_timetable_backend.service.MasterDataService;

@Component
public class TeachersDataLoader {
	private final MasterDataService masterDataService;

	public TeachersDataLoader(MasterDataService service) {
		this.masterDataService = service;
	}

	public TeachersData loadTeachers() {
		return masterDataService.getTeachersData();
	}
}
