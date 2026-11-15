
package rdpl_timetable_backend.validator;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import rdpl_timetable_backend.loader.TeachersDataLoader;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeacherAssignment;
import rdpl_timetable_backend.model.TeachersData;

@Component
public class TeacherAssignmentValidator {

	private final TeachersDataLoader teachersDataLoader;

	public TeacherAssignmentValidator(TeachersDataLoader teachersDataLoader) {
		this.teachersDataLoader = teachersDataLoader;
	}

	public void validate(Section section, String subjectName, String teacherId, String type) {

		if (section == null) {
			throw new IllegalArgumentException("Section is required.");
		}

		if (teacherId == null || teacherId.isBlank()) {
			throw new IllegalArgumentException("Teacher ID is required.");
		}

		if (subjectName == null || subjectName.isBlank()) {
			throw new IllegalArgumentException("Subject or activity is required.");
		}

		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("Assignment type is required.");
		}

		TeachersData data = teachersDataLoader.loadTeachers();

		if (data.getTeachers() == null) {
			throw new IllegalStateException("Teacher data is missing.");
		}

		Teacher teacher = data.getTeachers().stream().filter(Objects::nonNull)
				.filter(t -> teacherId.equals(t.getTeacherId())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

		List<TeacherAssignment> assignments = teacher.getAssignments();

		if (assignments == null || assignments.isEmpty()) {
			throw new IllegalArgumentException("Teacher has no assignments: " + teacherId);
		}

		boolean validAssignment = assignments.stream().filter(Objects::nonNull)
				.anyMatch(assignment -> assignment.getPeriodsPerWeek() > 0 && matchesClass(section, assignment)
						&& subjectName.trim().equalsIgnoreCase(assignment.getSubjectOrActivity().trim())
						&& type.trim().equalsIgnoreCase(assignment.getType().trim()));

		if (!validAssignment) {
			throw new IllegalArgumentException("Invalid teacher assignment: " + teacherId + " is not assigned to "
					+ subjectName + " (" + type + ") for " + section.getClassName());
		}
	}

	private boolean matchesClass(Section section, TeacherAssignment assignment) {

		if (section.getGrade() <= 0 || assignment.getClassName() == null) {
			return false;
		}

		String expectedClass = "Class " + section.getGrade();

		return expectedClass.equalsIgnoreCase(assignment.getClassName().trim());
	}
}
