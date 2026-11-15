
package rdpl_timetable_backend.config;

public class SolverConfig {

	private int maxSolvingTimeSeconds = 60;

	private int numberOfSearchWorkers = 8;

	private boolean enforceTeacherAvailability = true;

	private boolean enforceTeacherDoubleBooking = true;

	private boolean enforceSectionDoubleBooking = true;

	private boolean enforceWeeklySubjectQuota = true;

	private boolean enforceDailySubjectLimit = true;

	private boolean enforceConsecutivePeriods = true;

	private boolean enableSoftConstraints = true;

	public SolverConfig() {
	}

	public int getMaxSolvingTimeSeconds() {
		return maxSolvingTimeSeconds;
	}

	public void setMaxSolvingTimeSeconds(int maxSolvingTimeSeconds) {
		this.maxSolvingTimeSeconds = maxSolvingTimeSeconds;
	}

	public int getNumberOfSearchWorkers() {
		return numberOfSearchWorkers;
	}

	public void setNumberOfSearchWorkers(int numberOfSearchWorkers) {
		this.numberOfSearchWorkers = numberOfSearchWorkers;
	}

	public boolean isEnforceTeacherAvailability() {
		return enforceTeacherAvailability;
	}

	public void setEnforceTeacherAvailability(boolean enforceTeacherAvailability) {
		this.enforceTeacherAvailability = enforceTeacherAvailability;
	}

	public boolean isEnforceTeacherDoubleBooking() {
		return enforceTeacherDoubleBooking;
	}

	public void setEnforceTeacherDoubleBooking(boolean enforceTeacherDoubleBooking) {
		this.enforceTeacherDoubleBooking = enforceTeacherDoubleBooking;
	}

	public boolean isEnforceSectionDoubleBooking() {
		return enforceSectionDoubleBooking;
	}

	public void setEnforceSectionDoubleBooking(boolean enforceSectionDoubleBooking) {
		this.enforceSectionDoubleBooking = enforceSectionDoubleBooking;
	}

	public boolean isEnforceWeeklySubjectQuota() {
		return enforceWeeklySubjectQuota;
	}

	public void setEnforceWeeklySubjectQuota(boolean enforceWeeklySubjectQuota) {
		this.enforceWeeklySubjectQuota = enforceWeeklySubjectQuota;
	}

	public boolean isEnforceDailySubjectLimit() {
		return enforceDailySubjectLimit;
	}

	public void setEnforceDailySubjectLimit(boolean enforceDailySubjectLimit) {
		this.enforceDailySubjectLimit = enforceDailySubjectLimit;
	}

	public boolean isEnforceConsecutivePeriods() {
		return enforceConsecutivePeriods;
	}

	public void setEnforceConsecutivePeriods(boolean enforceConsecutivePeriods) {
		this.enforceConsecutivePeriods = enforceConsecutivePeriods;
	}

	public boolean isEnableSoftConstraints() {
		return enableSoftConstraints;
	}

	public void setEnableSoftConstraints(boolean enableSoftConstraints) {
		this.enableSoftConstraints = enableSoftConstraints;
	}
}
