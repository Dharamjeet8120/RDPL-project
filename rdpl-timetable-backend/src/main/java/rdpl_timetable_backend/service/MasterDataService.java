
package rdpl_timetable_backend.service;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rdpl_timetable_backend.model.ClassCurriculum;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.CurriculumItem;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.SectionMetadata;
import rdpl_timetable_backend.model.SectionsData;
import rdpl_timetable_backend.model.Teacher;
import rdpl_timetable_backend.model.TeacherAssignment;
import rdpl_timetable_backend.model.TeachersData;
import rdpl_timetable_backend.repository.master.CurriculumRepository;
import rdpl_timetable_backend.repository.master.SectionMetadataRepository;
import rdpl_timetable_backend.repository.master.SectionRepository;
import rdpl_timetable_backend.repository.master.TeacherRepository;
import tools.jackson.databind.ObjectMapper;

@Service
@Transactional
public class MasterDataService {
	private static final String SECTION_META_ID = "default";
	private final TeacherRepository teacherRepository;
	private final SectionRepository sectionRepository;
	private final CurriculumRepository curriculumRepository;
	private final SectionMetadataRepository metadataRepository;
	private final ObjectMapper mapper;

	public MasterDataService(TeacherRepository teacherRepository, SectionRepository sectionRepository,
			CurriculumRepository curriculumRepository, SectionMetadataRepository metadataRepository,
			ObjectMapper mapper) {
		this.teacherRepository = teacherRepository;
		this.sectionRepository = sectionRepository;
		this.curriculumRepository = curriculumRepository;
		this.metadataRepository = metadataRepository;
		this.mapper = mapper;
	}

	@Transactional(readOnly = true)
	public TeachersData getTeachersData() {
		List<Teacher> teachers = teacherRepository.findAll();
		SectionMetadata meta = metadataRepository.findById(SECTION_META_ID)
				.orElse(new SectionMetadata("2026-2027", 52));
		int assigned = (int) teachers.stream().filter(t -> t.getAssignments() != null && !t.getAssignments().isEmpty())
				.count();
		return new TeachersData(meta.getAcademicYear(), teachers.size(), assigned, teachers.size() - assigned,
				teachers);
	}

	@Transactional(readOnly = true)
	public SectionsData getSectionsData() {
		List<Section> sections = sectionRepository.findAll();
		SectionMetadata meta = metadataRepository.findById(SECTION_META_ID)
				.orElse(new SectionMetadata("2026-2027", 52));
		SectionsData data = new SectionsData();
		data.setAcademicYear(meta.getAcademicYear());
		data.setWeeklyTeachingCapacity(meta.getWeeklyTeachingCapacity());
		data.setTotalSections(sections.size());
		data.setSections(sections);
		return data;
	}

	@Transactional(readOnly = true)
	public CurriculumData getCurriculumData() {
		return curriculumRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new IllegalStateException("Curriculum data is not imported."));
	}

	public Teacher createTeacher(Teacher teacher) {
		validateTeacher(teacher);
		if (teacherRepository.existsById(teacher.getTeacherId()))
			throw new IllegalArgumentException("Teacher ID already exists: " + teacher.getTeacherId());
		if (teacher.getAssignments() != null)
			for (TeacherAssignment row : teacher.getAssignments())
				validateAssignment(row);
		teacher.setAssignments(teacher.getAssignments());
		return teacherRepository.save(teacher);
	}

	public Teacher updateTeacher(String id, Teacher input) {
		Teacher current = teacherRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id));
		if (input == null)
			throw new IllegalArgumentException("Teacher body is required.");
		if (input.getTeacherName() == null || input.getTeacherName().isBlank())
			throw new IllegalArgumentException("Teacher name is required.");
		current.setTeacherName(input.getTeacherName().trim());
		current.setTotalPeriodsPerWeek(input.getTotalPeriodsPerWeek());
		current.setMaxPeriodsPerDay(input.getMaxPeriodsPerDay());
		current.setMaxPeriodsPerWeek(input.getMaxPeriodsPerWeek());
		if (input.getAssignments() != null) {
			current.getAssignments().clear();
			for (TeacherAssignment row : input.getAssignments()) {
				validateAssignment(row);
				row.setId(null);
				current.addAssignment(row);
			}
		}
		return teacherRepository.save(current);
	}

	public Teacher replaceAssignments(String teacherId, List<TeacherAssignment> assignments) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
		teacher.getAssignments().clear();
		if (assignments != null)
			for (TeacherAssignment row : assignments) {
				validateAssignment(row);
				row.setId(null);
				teacher.addAssignment(row);
			}
		return teacherRepository.save(teacher);
	}

	public TeacherAssignment addAssignment(String teacherId, TeacherAssignment assignment) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
		validateAssignment(assignment);
		assignment.setId(null);
		teacher.addAssignment(assignment);
		teacherRepository.save(teacher);
		return teacher.getAssignments().get(teacher.getAssignments().size() - 1);
	}

	public TeacherAssignment updateAssignment(String teacherId, Long assignmentId, TeacherAssignment input) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
		TeacherAssignment current = teacher.getAssignments().stream().filter(a -> assignmentId.equals(a.getId()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Teacher assignment not found: " + assignmentId));
		validateAssignment(input);
		current.setClassName(input.getClassName());
		current.setSectionId(input.getSectionId());
		current.setSectionName(input.getSectionName());
		current.setType(input.getType());
		current.setSubjectOrActivity(input.getSubjectOrActivity());
		current.setPeriodsPerWeek(input.getPeriodsPerWeek());
		teacherRepository.save(teacher);
		return current;
	}

	public void deleteAssignment(String teacherId, Long assignmentId) {
		Teacher teacher = teacherRepository.findById(teacherId)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));
		boolean removed = teacher.getAssignments().removeIf(a -> assignmentId.equals(a.getId()));
		if (!removed)
			throw new IllegalArgumentException("Teacher assignment not found: " + assignmentId);
		teacherRepository.save(teacher);
	}

	public void deleteTeacher(String id) {
		Teacher teacher = teacherRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id));
		teacherRepository.delete(teacher);
	}

	@Transactional(readOnly = true)
	public Teacher getTeacher(String id) {
		return teacherRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + id));
	}

	@Transactional(readOnly = true)
	public Section getSection(String id) {
		return sectionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
	}

	public Section createSection(Section section) {
		validateSection(section);
		if (sectionRepository.existsById(section.getId()))
			throw new IllegalArgumentException("Section ID already exists: " + section.getId());
		return sectionRepository.save(section);
	}

	public Section updateSection(String id, Section input) {
		Section current = sectionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
		if (input != null)
			input.setId(id);
		validateSection(input);
		current.setClassName(input.getClassName());
		current.setSectionName(input.getSectionName());
		current.setGrade(input.getGrade());
		current.setCapacity(input.getCapacity());
		current.setCurriculumKey(input.getCurriculumKey());
		return sectionRepository.save(current);
	}

	public void deleteSection(String id) {
		Section section = sectionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
		sectionRepository.delete(section);
	}

	@Transactional(readOnly = true)
	public ClassCurriculum getCurriculumClass(String className) {
		return getCurriculumData().getClasses().stream().filter(c -> c.getClassName().equalsIgnoreCase(className))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Curriculum class not found: " + className));
	}

	public ClassCurriculum saveCurriculumClass(String className, ClassCurriculum input) {
		if (className == null || className.isBlank() || input == null)
			throw new IllegalArgumentException("Class name and curriculum body are required.");
		if (input.getItems() == null || input.getItems().isEmpty())
			throw new IllegalArgumentException("At least one curriculum item is required.");
		for (CurriculumItem item : input.getItems()) {
			if (item == null || item.getName() == null || item.getName().isBlank() || item.getType() == null
					|| item.getType().isBlank())
				throw new IllegalArgumentException("Every curriculum item requires type and name.");
			if (item.getPeriodsPerDay() < 0 || item.getPeriodsPerWeek() < 0)
				throw new IllegalArgumentException("Curriculum periods cannot be negative.");
		}
		CurriculumData root = getCurriculumData();
		ClassCurriculum old = root.getClasses().stream().filter(c -> c.getClassName().equalsIgnoreCase(className))
				.findFirst().orElse(null);
		if (old != null) {
			old.setTotalPeriodsPerWeek(input.getTotalPeriodsPerWeek());
			old.setSubjectPeriodsPerWeek(input.getSubjectPeriodsPerWeek());
			old.setActivityPeriodsPerWeek(input.getActivityPeriodsPerWeek());
			old.getItems().clear();
			if (input.getItems() != null)
				for (CurriculumItem item : input.getItems())
					old.addItem(new CurriculumItem(item.getType(), item.getName(), item.getPeriodsPerDay(),
							item.getPeriodsPerWeek()));
			curriculumRepository.save(root);
			return old;
		}
		input.setId(null);
		input.setClassName(className.trim());
		root.addClass(input);
		curriculumRepository.save(root);
		return input;
	}

	public void deleteCurriculumClass(String className) {
		CurriculumData root = getCurriculumData();
		boolean removed = root.getClasses().removeIf(c -> c.getClassName().equalsIgnoreCase(className));
		if (!removed)
			throw new IllegalArgumentException("Curriculum class not found: " + className);
		curriculumRepository.save(root);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> status() {
		Map<String, Object> out = new LinkedHashMap<>();
		out.put("teachers", teacherRepository.count());
		out.put("sections", sectionRepository.count());
		CurriculumData curriculum = curriculumRepository.findAll().stream().findFirst().orElse(null);
		out.put("curriculumClasses", curriculum == null ? 0 : curriculum.getClasses().size());
		out.put("imported", teacherRepository.count() > 0 && sectionRepository.count() > 0 && curriculum != null);
		out.put("storage", "MYSQL");
		return out;
	}

	/**
	 * Imports the bundled JSON master files as a one-time bootstrap. Existing data
	 * is preserved.
	 */
	public Map<String, Object> importJsonIfEmpty() {
		boolean empty = teacherRepository.count() == 0 && sectionRepository.count() == 0
				&& curriculumRepository.count() == 0;
		if (!empty)
			return statusWithMessage("Existing MySQL master data found; no import performed.", false);
		return importJsonReplacingAll();
	}

	/**
	 * Explicit import endpoint: replaces master tables with the bundled JSON
	 * baseline.
	 */
	public Map<String, Object> importJsonReplacingAll() {
		try {
			TeachersData teachers = read("data/teachers.json", TeachersData.class);
			SectionsData sections = read("data/sections.json", SectionsData.class);
			CurriculumData curriculum = read("data/curriculum.json", CurriculumData.class);
			// Remove the old rows first and make sure the deletes reach MySQL before the
			// new rows are inserted.
			teacherRepository.deleteAll();
			sectionRepository.deleteAll();
			curriculumRepository.deleteAll();
			teacherRepository.flush();
			sectionRepository.flush();
			curriculumRepository.flush();
			if (teachers.getTeachers() != null)
				for (Teacher t : teachers.getTeachers()) {
					t.setAssignments(t.getAssignments());
					teacherRepository.save(t);
				}
			if (sections.getSections() != null)
				sectionRepository.saveAll(sections.getSections());
			if (curriculum.getClasses() != null)
				curriculum.setClasses(curriculum.getClasses());
			curriculumRepository.save(curriculum);
			metadataRepository
					.save(new SectionMetadata(sections.getAcademicYear(), sections.getWeeklyTeachingCapacity()));
			Map<String, Object> result = statusWithMessage("JSON master data imported into MySQL successfully.", true);
			result.put("teacherRecords", teacherRepository.count());
			result.put("sectionRecords", sectionRepository.count());
			result.put("curriculumClasses", curriculum.getClasses() == null ? 0 : curriculum.getClasses().size());
			return result;
		} catch (Exception e) {
			throw new IllegalStateException("Unable to import master JSON data: " + e.getMessage(), e);
		}
	}

	private <T> T read(String path, Class<T> type) throws Exception {
		try (InputStream in = new ClassPathResource(path).getInputStream()) {
			return mapper.readValue(in, type);
		}
	}

	private Map<String, Object> statusWithMessage(String message, boolean imported) {
		Map<String, Object> m = new LinkedHashMap<>(status());
		m.put("message", message);
		m.put("importedNow", imported);
		return m;
	}

	private void validateAssignment(TeacherAssignment a) {
		if (a == null || a.getClassName() == null || a.getClassName().isBlank() || a.getSubjectOrActivity() == null
				|| a.getSubjectOrActivity().isBlank() || a.getType() == null || a.getType().isBlank())
			throw new IllegalArgumentException("Assignment class, type and subject/activity are required.");
		if (a.getPeriodsPerWeek() < 0)
			throw new IllegalArgumentException("Assignment periods per week cannot be negative.");
	}

	private void validateTeacher(Teacher t) {
		if (t == null || t.getTeacherId() == null || t.getTeacherId().isBlank() || t.getTeacherName() == null
				|| t.getTeacherName().isBlank())
			throw new IllegalArgumentException("Teacher ID and teacher name are required.");
	}

	private void validateSection(Section s) {
		if (s == null || s.getId() == null || s.getId().isBlank() || s.getClassName() == null
				|| s.getClassName().isBlank() || s.getSectionName() == null || s.getSectionName().isBlank())
			throw new IllegalArgumentException("Section ID, class name and section name are required.");
		if (s.getCapacity() < 0)
			throw new IllegalArgumentException("Section capacity cannot be negative.");
	}
}
