package rdpl_timetable_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.dto.LoadedSchoolData;
import rdpl_timetable_backend.service.DataLoadingService;

@RestController
@RequestMapping("/api/timetable")
public class DataLoadingController {

	private final DataLoadingService dataLoadingService;

	public DataLoadingController(DataLoadingService dataLoadingService) {
		this.dataLoadingService = dataLoadingService;
	}

	@GetMapping("/load-all-data")
	public LoadedSchoolData loadAllData() {

		return new LoadedSchoolData(dataLoadingService.loadSections(), dataLoadingService.loadBellSchedule(),
				dataLoadingService.loadSchedulingRules(), dataLoadingService.loadSchoolSample());
	}
}