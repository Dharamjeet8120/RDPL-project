
package rdpl_timetable_backend.validator;

import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.ClassCurriculum;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.DataValidationResult;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.SectionsData;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeachersData;

@Component
public class DataValidator {

	public DataValidationResult validate(SectionsData sectionsData, TeachersData teachersData,
			CurriculumData curriculumData) {

		DataValidationResult result = new DataValidationResult();

		validateSections(sectionsData, result);
		validateTeachers(teachersData, result);
		validateCurriculum(curriculumData, result);

		result.setValid(result.getTotalErrors() == 0);

		return result;
	}

	private void validateSections(SectionsData data, DataValidationResult result) {

		if (data == null || data.getSections() == null) {
			result.addError("Sections data is missing.");
			return;
		}

		if (data.getSections().size() != 41) {
			result.addWarning("Expected 41 sections but found " + data.getSections().size());
		}

		for (Section section : data.getSections()) {

			if (section.getId() == null || section.getId().isBlank()) {
				result.addError("A section has missing ID.");
			}

			if (section.getClassName() == null || section.getClassName().isBlank()) {
				result.addError("Section " + section.getId() + " has missing class name.");
			}

			if (section.getSectionName() == null || section.getSectionName().isBlank()) {
				result.addError("Section " + section.getId() + " has missing section name.");
			}
		}
	}

	private void validateTeachers(TeachersData data, DataValidationResult result) {

		if (data == null || data.getTeachers() == null) {
			result.addError("Teachers data is missing.");
			return;
		}

		if (data.getTeachers().size() != 56) {
			result.addWarning("Expected 56 teachers but found " + data.getTeachers().size());
		}

		for (Teacher teacher : data.getTeachers()) {

			if (teacher.getTeacherId() == null || teacher.getTeacherId().isBlank()) {
				result.addError("A teacher has missing ID.");
			}

			if (teacher.getTeacherName() == null || teacher.getTeacherName().isBlank()) {
				result.addError("Teacher " + teacher.getTeacherId() + " has missing name.");
			}

			if (teacher.getTotalPeriodsPerWeek() > 0 && teacher.getAssignments() != null
					&& teacher.getAssignments().isEmpty()) {
				result.addWarning("Teacher " + teacher.getTeacherName() + " has workload information but no "
						+ "detailed assignments.");
			}
		}

		result.addWarning(
				"Section-level teacher assignments are not available. " + "Teacher scheduling cannot be finalized.");

		result.addWarning("92 UNASSIGNED-TT records must be resolved " + "before final timetable generation.");

		result.addWarning("142 zero-workload assignment records require review.");
	}

	private void validateCurriculum(CurriculumData data, DataValidationResult result) {

		if (data == null || data.getClasses() == null) {
			result.addError("Curriculum data is missing.");
			return;
		}

		if (data.getWeeklyTeachingCapacity() != 52) {
			result.addError("Weekly teaching capacity must be 52.");
		}

		if (data.getClasses().size() != 12) {
			result.addWarning("Expected 12 curriculum classes but found " + data.getClasses().size());
		}

		for (ClassCurriculum curriculum : data.getClasses()) {

			if (curriculum.getItems() == null) {
				result.addError(curriculum.getClassName() + " has no curriculum items.");
				continue;
			}

			int totalPeriods = curriculum.getItems().stream().mapToInt(item -> item.getPeriodsPerWeek()).sum();

			if (totalPeriods != curriculum.getTotalPeriodsPerWeek()) {
				result.addError(curriculum.getClassName() + " curriculum total mismatch. Expected "
						+ curriculum.getTotalPeriodsPerWeek() + " but found " + totalPeriods);
			}

			if (totalPeriods != 52) {
				result.addError(curriculum.getClassName() + " does not have 52 weekly periods.");
			}
		}

		result.addWarning("Pre-Nursery, Nursery, Junior KG and Senior KG " + "curriculum is not available.");

		result.addWarning(
				"Some periods-per-day values are provisional " + "and need confirmation from official rules.");
	}
}
