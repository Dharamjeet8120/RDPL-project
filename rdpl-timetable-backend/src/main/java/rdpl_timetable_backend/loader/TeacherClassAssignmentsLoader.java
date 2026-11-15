
package rdpl_timetable_backend.loader;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.TeacherAssignment;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class TeacherClassAssignmentsLoader {

	private final ObjectMapper objectMapper;

	public TeacherClassAssignmentsLoader(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public TeacherClassAssignmentsData loadAssignments() {

		try {
			ClassPathResource resource = new ClassPathResource("data/teacher-class-assignments.json");

			try (InputStream inputStream = resource.getInputStream()) {

				TeacherClassAssignmentsData data = objectMapper.readValue(inputStream,
						TeacherClassAssignmentsData.class);

				if (data.getTeachers() == null) {
					throw new IllegalStateException("Teacher assignments list is missing.");
				}

				return data;
			}

		} catch (IOException e) {
			throw new RuntimeException("Unable to load teacher-class-assignments.json: " + e.getMessage(), e);
		}
	}

	public static class TeacherClassAssignmentsData {

		private String academicYear;
		private int totalAssignmentRows;
		private List<TeacherClassRecord> teachers;

		public TeacherClassAssignmentsData() {
		}

		public String getAcademicYear() {
			return academicYear;
		}

		public void setAcademicYear(String academicYear) {
			this.academicYear = academicYear;
		}

		public int getTotalAssignmentRows() {
			return totalAssignmentRows;
		}

		public void setTotalAssignmentRows(int totalAssignmentRows) {
			this.totalAssignmentRows = totalAssignmentRows;
		}

		public List<TeacherClassRecord> getTeachers() {
			return teachers;
		}

		public void setTeachers(List<TeacherClassRecord> teachers) {
			this.teachers = teachers;
		}
	}

	public static class TeacherClassRecord {

		private String teacherId;
		private String teacherName;
		private List<TeacherAssignment> assignments;

		public TeacherClassRecord() {
		}

		public String getTeacherId() {
			return teacherId;
		}

		public void setTeacherId(String teacherId) {
			this.teacherId = teacherId;
		}

		public String getTeacherName() {
			return teacherName;
		}

		public void setTeacherName(String teacherName) {
			this.teacherName = teacherName;
		}

		public List<TeacherAssignment> getAssignments() {
			return assignments;
		}

		public void setAssignments(List<TeacherAssignment> assignments) {
			this.assignments = assignments;
		}
	}
}
