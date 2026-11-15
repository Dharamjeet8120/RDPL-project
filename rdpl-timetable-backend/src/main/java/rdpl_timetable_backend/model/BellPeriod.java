
package rdpl_timetable_backend.model;

public class BellPeriod {

	private String name;
	private String startTime;
	private String endTime;
	private String type;
	private int periodNumber;

	public BellPeriod() {
	}

	public BellPeriod(String name, String startTime, String endTime, String type, int periodNumber) {

		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.type = type;
		this.periodNumber = periodNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPeriodNumber() {
		return periodNumber;
	}

	public void setPeriodNumber(int periodNumber) {
		this.periodNumber = periodNumber;
	}
}
