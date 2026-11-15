
package rdpl_timetable_backend.model;

import java.util.ArrayList;
import java.util.List;

public class DataValidationResult {

	private boolean valid;

	private int totalErrors;

	private int totalWarnings;

	private List<String> errors;

	private List<String> warnings;

	public DataValidationResult() {
		this.errors = new ArrayList<>();
		this.warnings = new ArrayList<>();
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getTotalErrors() {
		return totalErrors;
	}

	public void setTotalErrors(int totalErrors) {
		this.totalErrors = totalErrors;
	}

	public int getTotalWarnings() {
		return totalWarnings;
	}

	public void setTotalWarnings(int totalWarnings) {
		this.totalWarnings = totalWarnings;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	public void addError(String error) {
		errors.add(error);
		totalErrors = errors.size();
	}

	public void addWarning(String warning) {
		warnings.add(warning);
		totalWarnings = warnings.size();
	}
}
