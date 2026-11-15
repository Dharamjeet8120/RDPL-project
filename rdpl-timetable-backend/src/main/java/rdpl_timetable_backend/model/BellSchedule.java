
package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BellSchedule {

	private String scheduleName;

	private String academicYear;

	private List<String> workingDays = new ArrayList<>();

	private Map<String, Integer> dayNumbering = new HashMap<>();

	private int weeklyTeachingCapacity;

	private List<DailySchedule> dailySchedules = new ArrayList<>();

	public BellSchedule() {
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public List<String> getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(List<String> workingDays) {
		this.workingDays = workingDays;
	}

	public Map<String, Integer> getDayNumbering() {
		return dayNumbering;
	}

	public void setDayNumbering(Map<String, Integer> dayNumbering) {
		this.dayNumbering = dayNumbering;
	}

	public int getWeeklyTeachingCapacity() {
		return weeklyTeachingCapacity;
	}

	public void setWeeklyTeachingCapacity(int weeklyTeachingCapacity) {
		this.weeklyTeachingCapacity = weeklyTeachingCapacity;
	}

	public List<DailySchedule> getDailySchedules() {
		return dailySchedules;
	}

	public void setDailySchedules(List<DailySchedule> dailySchedules) {
		this.dailySchedules = dailySchedules;
	}
}
