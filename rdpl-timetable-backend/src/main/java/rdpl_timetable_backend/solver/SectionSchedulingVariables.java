
package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.List;

public class SectionSchedulingVariables {

	private String sectionId;

	private List<SchedulingVariable> variables;

	public SectionSchedulingVariables() {
		this.variables = new ArrayList<>();
	}

	public SectionSchedulingVariables(String sectionId) {
		this.sectionId = sectionId;
		this.variables = new ArrayList<>();
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public List<SchedulingVariable> getVariables() {
		return variables;
	}

	public void setVariables(List<SchedulingVariable> variables) {
		this.variables = variables;
	}

	public void addVariable(SchedulingVariable variable) {
		this.variables.add(variable);
	}

	public int getTotalVariables() {
		return variables.size();
	}
}
