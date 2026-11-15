package rdpl_timetable_backend.service;

import org.springframework.stereotype.Service;

import rdpl_timetable_backend.loader.JsonDataLoader;
import rdpl_timetable_backend.loader.BellScheduleLoader;
import rdpl_timetable_backend.loader.SchedulingRulesLoader;
import rdpl_timetable_backend.loader.SchoolSampleLoader;

import rdpl_timetable_backend.model.SectionsData;
import rdpl_timetable_backend.model.BellSchedule;
import rdpl_timetable_backend.model.SchedulingRules;
import rdpl_timetable_backend.model.SchoolSample;

@Service
public class DataLoadingService {

	private final JsonDataLoader jsonDataLoader;
	private final BellScheduleLoader bellScheduleLoader;
	private final SchedulingRulesLoader schedulingRulesLoader;
	private final SchoolSampleLoader schoolSampleLoader;

	public DataLoadingService(JsonDataLoader jsonDataLoader, BellScheduleLoader bellScheduleLoader,
			SchedulingRulesLoader schedulingRulesLoader, SchoolSampleLoader schoolSampleLoader) {

		this.jsonDataLoader = jsonDataLoader;
		this.bellScheduleLoader = bellScheduleLoader;
		this.schedulingRulesLoader = schedulingRulesLoader;
		this.schoolSampleLoader = schoolSampleLoader;
	}

	public SectionsData loadSections() {
		return jsonDataLoader.loadSections();
	}

	public BellSchedule loadBellSchedule() {
		return bellScheduleLoader.loadBellSchedule();
	}

	public SchedulingRules loadSchedulingRules() {
		return schedulingRulesLoader.loadSchedulingRules();
	}

	public SchoolSample loadSchoolSample() {
		return schoolSampleLoader.loadSchoolSample();
	}
}