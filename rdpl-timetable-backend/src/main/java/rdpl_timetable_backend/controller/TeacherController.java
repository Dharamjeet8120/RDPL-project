package rdpl_timetable_backend.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeacherAssignment;
import rdpl_timetable_backend.service.MasterDataService;
import rdpl_timetable_backend.service.TeacherAssignmentValidationService;

@RestController
@RequestMapping("/api/timetable")
public class TeacherController {
	private final MasterDataService masterDataService;
	private final TeacherAssignmentValidationService validationService;

	public TeacherController(MasterDataService service, TeacherAssignmentValidationService validationService) {
		this.masterDataService = service;
		this.validationService = validationService;
	}

	@GetMapping("/teachers")
	public List<Teacher> getAllTeachers() {
		return masterDataService.getTeachersData().getTeachers();
	}

	@GetMapping("/teachers/count")
	public int getTeacherCount() {
		return masterDataService.getTeachersData().getTeachers().size();
	}

	@GetMapping("/teachers/academic-year")
	public String getAcademicYear() {
		return masterDataService.getTeachersData().getAcademicYear();
	}

	@GetMapping("/teachers/assignment-validation")
	public Map<String, Object> validateTeacherAssignments() {
		return validationService.validate(masterDataService.getTeachersData());
	}

	@GetMapping("/teachers/{id}")
	public Teacher getTeacher(@PathVariable String id) {
		return masterDataService.getTeacher(id);
	}

	@PostMapping("/teachers")
	@ResponseStatus(HttpStatus.CREATED)
	public Teacher createTeacher(@RequestBody Teacher teacher) {
		return masterDataService.createTeacher(teacher);
	}

	@PutMapping("/teachers/{id}")
	public Teacher updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
		return masterDataService.updateTeacher(id, teacher);
	}

	@GetMapping("/teachers/{id}/assignments")
	public List<TeacherAssignment> getAssignments(@PathVariable String id) {
		return masterDataService.getTeacher(id).getAssignments();
	}

	@PostMapping("/teachers/{id}/assignments")
	@ResponseStatus(HttpStatus.CREATED)
	public TeacherAssignment addAssignment(@PathVariable String id, @RequestBody TeacherAssignment assignment) {
		return masterDataService.addAssignment(id, assignment);
	}

	@PutMapping("/teachers/{id}/assignments/{assignmentId}")
	public TeacherAssignment updateAssignment(@PathVariable String id, @PathVariable Long assignmentId,
			@RequestBody TeacherAssignment assignment) {
		return masterDataService.updateAssignment(id, assignmentId, assignment);
	}

	@DeleteMapping("/teachers/{id}/assignments/{assignmentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAssignment(@PathVariable String id, @PathVariable Long assignmentId) {
		masterDataService.deleteAssignment(id, assignmentId);
	}

	@PutMapping("/teachers/{id}/assignments")
	public Teacher replaceAssignments(@PathVariable String id, @RequestBody List<TeacherAssignment> assignments) {
		return masterDataService.replaceAssignments(id, assignments);
	}

	@DeleteMapping("/teachers/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTeacher(@PathVariable String id) {
		masterDataService.deleteTeacher(id);
	}
}
