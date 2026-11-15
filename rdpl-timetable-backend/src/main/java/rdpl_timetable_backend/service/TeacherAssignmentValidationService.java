
package rdpl_timetable_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeachersData;
import rdpl_timetable_backend.model.TeacherAssignment;

@Service
public class TeacherAssignmentValidationService {

	public Map<String, Object> validate(TeachersData teachersData) {

		if (teachersData == null || teachersData.getTeachers() == null) {
			throw new IllegalArgumentException("Teacher data is missing.");
		}

		List<Teacher> teachers = teachersData.getTeachers();

		int totalTeachers = teachers.size();
		int teachersWithAssignments = 0;
		int teachersWithoutAssignments = 0;
		int totalAssignmentRows = 0;
		int sectionLevelAssignments = 0;
		int classLevelAssignments = 0;
		int invalidAssignments = 0;

		List<String> warnings = new ArrayList<>();

		for (Teacher teacher : teachers) {

			if (teacher == null) {
				warnings.add("Null teacher record found.");
				continue;
			}

			List<TeacherAssignment> assignments = teacher.getAssignments();

			if (assignments == null || assignments.isEmpty()) {
				teachersWithoutAssignments++;
				continue;
			}

			teachersWithAssignments++;

			for (TeacherAssignment assignment : assignments) {

				if (assignment == null) {
					invalidAssignments++;
					continue;
				}

				totalAssignmentRows++;

				if (assignment.getSectionId() != null && !assignment.getSectionId().isBlank()) {

					sectionLevelAssignments++;

				} else if (assignment.getClassName() != null && !assignment.getClassName().isBlank()) {

					classLevelAssignments++;

				} else {

					invalidAssignments++;
					warnings.add("Assignment has no sectionId or className for teacher: " + teacher.getTeacherId());
				}
			}
		}

		if (totalAssignmentRows == 0) {
			warnings.add("No detailed teacher assignments are available. "
					+ "Timetable generation with actual teachers is not ready.");
		}

		Map<String, Object> report = new HashMap<>();

		report.put("totalTeachers", totalTeachers);
		report.put("teachersWithAssignments", teachersWithAssignments);
		report.put("teachersWithoutAssignments", teachersWithoutAssignments);
		report.put("totalAssignmentRows", totalAssignmentRows);
		report.put("sectionLevelAssignments", sectionLevelAssignments);
		report.put("classLevelAssignments", classLevelAssignments);
		report.put("invalidAssignments", invalidAssignments);
		report.put("readyForTeacherScheduling", totalAssignmentRows > 0 && sectionLevelAssignments > 0);
		report.put("warnings", warnings);

		return report;
	}
}
