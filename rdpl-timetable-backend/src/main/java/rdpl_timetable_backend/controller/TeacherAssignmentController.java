package rdpl_timetable_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.SectionSubjectQuota;
import rdpl_timetable_backend.service.TeacherAssignmentService;
import rdpl_timetable_backend.service.QuotaValidationService;

@RestController
@RequestMapping("/api/timetable/teacher-assignments")
public class TeacherAssignmentController {

	private final TeacherAssignmentService teacherAssignmentService;
	private final QuotaValidationService quotaValidationService;

	public TeacherAssignmentController(TeacherAssignmentService teacherAssignmentService,
			QuotaValidationService quotaValidationService) {

		this.teacherAssignmentService = teacherAssignmentService;
		this.quotaValidationService = quotaValidationService;
	}

	// Eligible Teachers API
	@GetMapping("/eligible")
	public List<Teacher> getEligibleTeachers(@RequestParam String className, @RequestParam String subjectName,
			@RequestParam String type) {

		String normalizedType = "THEORY".equalsIgnoreCase(type) ? "SUBJECT" : type;

		return teacherAssignmentService.findEligibleTeachers(className, subjectName, normalizedType);
	}

	// Unassigned Subjects API
	@GetMapping("/unassigned")
	public List<SectionSubjectQuota> getUnassignedQuotas() {

		return quotaValidationService.findUnassignedQuotas();
	}
}