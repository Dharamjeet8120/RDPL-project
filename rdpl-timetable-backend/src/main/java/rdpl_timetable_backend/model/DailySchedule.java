
package rdpl_timetable_backend.model;

import java.util.List;

public class DailySchedule {

	private List<String> days;

	private List<TimePeriod> teachingPeriods;

	private List<BreakPeriod> breaks;

	private List<Integer> morningPeriods;

	private List<Integer> afterLunchPeriods;

	public DailySchedule() {
	}

	public List<String> getDays() {
		return days;
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

	public List<TimePeriod> getTeachingPeriods() {
		return teachingPeriods;
	}

	public void setTeachingPeriods(List<TimePeriod> teachingPeriods) {
		this.teachingPeriods = teachingPeriods;
	}

	public List<BreakPeriod> getBreaks() {
		return breaks;
	}

	public void setBreaks(List<BreakPeriod> breaks) {
		this.breaks = breaks;
	}

	public List<Integer> getMorningPeriods() {
		return morningPeriods;
	}

	public void setMorningPeriods(List<Integer> morningPeriods) {
		this.morningPeriods = morningPeriods;
	}

	public List<Integer> getAfterLunchPeriods() {
		return afterLunchPeriods;
	}

	public void setAfterLunchPeriods(List<Integer> afterLunchPeriods) {
		this.afterLunchPeriods = afterLunchPeriods;
	}
}
