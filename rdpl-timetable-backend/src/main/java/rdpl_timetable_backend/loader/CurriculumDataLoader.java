package rdpl_timetable_backend.loader;

import org.springframework.stereotype.Component;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.service.MasterDataService;

@Component
public class CurriculumDataLoader {
	private final MasterDataService masterDataService;

	public CurriculumDataLoader(MasterDataService service) {
		this.masterDataService = service;
	}

	public CurriculumData loadCurriculum() {
		return masterDataService.getCurriculumData();
	}
}
