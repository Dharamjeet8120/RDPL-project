package rdpl_timetable_backend.dto;

import rdpl_timetable_backend.model.SectionsData;
import rdpl_timetable_backend.model.BellSchedule;
import rdpl_timetable_backend.model.SchedulingRules;
import rdpl_timetable_backend.model.SchoolSample;

public class LoadedSchoolData {

	private SectionsData sections;
	private BellSchedule bellSchedule;
	private SchedulingRules schedulingRules;
	private SchoolSample schoolSample;

	public LoadedSchoolData() {
	}

	public LoadedSchoolData(SectionsData sections, BellSchedule bellSchedule, SchedulingRules schedulingRules,
			SchoolSample schoolSample) {

		this.sections = sections;
		this.bellSchedule = bellSchedule;
		this.schedulingRules = schedulingRules;
		this.schoolSample = schoolSample;
	}

	public SectionsData getSections() {
		return sections;
	}

	public void setSections(SectionsData sections) {
		this.sections = sections;
	}

	public BellSchedule getBellSchedule() {
		return bellSchedule;
	}

	public void setBellSchedule(BellSchedule bellSchedule) {
		this.bellSchedule = bellSchedule;
	}

	public SchedulingRules getSchedulingRules() {
		return schedulingRules;
	}

	public void setSchedulingRules(SchedulingRules schedulingRules) {
		this.schedulingRules = schedulingRules;
	}

	public SchoolSample getSchoolSample() {
		return schoolSample;
	}

	public void setSchoolSample(SchoolSample schoolSample) {
		this.schoolSample = schoolSample;
	}
}