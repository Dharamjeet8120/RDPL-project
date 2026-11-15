package rdpl_timetable_backend.loader;

import org.springframework.stereotype.Component;
import rdpl_timetable_backend.model.SectionsData;
import rdpl_timetable_backend.service.MasterDataService;

@Component
public class JsonDataLoader {
	private final MasterDataService masterDataService;

	public JsonDataLoader(MasterDataService service) {
		this.masterDataService = service;
	}

	public SectionsData loadSections() {
		return masterDataService.getSectionsData();
	}
}
