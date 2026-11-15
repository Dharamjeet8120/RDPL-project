
package rdpl_timetable_backend.model;

public class TimeSlot {

	private String day;

	private int periodNumber;

	private String periodLabel;

	private String startTime;

	private String endTime;

	public TimeSlot() {
	}

	public TimeSlot(String day, int periodNumber, String periodLabel, String startTime, String endTime) {

		this.day = day;
		this.periodNumber = periodNumber;
		this.periodLabel = periodLabel;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(int periodNumber) {
		this.periodNumber = periodNumber;
	}

	public String getPeriodLabel() {
		return periodLabel;
	}

	public void setPeriodLabel(String periodLabel) {
		this.periodLabel = periodLabel;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
