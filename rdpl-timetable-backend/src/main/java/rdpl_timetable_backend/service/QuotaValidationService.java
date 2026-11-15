
package rdpl_timetable_backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import rdpl_timetable_backend.loader.CurriculumDataLoader;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.SectionSubjectQuota;
import rdpl_timetable_backend.solver.SectionQuotaBuilder;
import rdpl_timetable_backend.loader.JsonDataLoader;
import rdpl_timetable_backend.model.SectionsData;

@Service
public class QuotaValidationService {

	private final SectionQuotaBuilder sectionQuotaBuilder;
	private final CurriculumDataLoader curriculumDataLoader;
	private final TeacherAssignmentService teacherAssignmentService;
	private final JsonDataLoader jsonDataLoader;

	public QuotaValidationService(SectionQuotaBuilder sectionQuotaBuilder, CurriculumDataLoader curriculumDataLoader,
			TeacherAssignmentService teacherAssignmentService, JsonDataLoader jsonDataLoader) {

		this.sectionQuotaBuilder = sectionQuotaBuilder;
		this.curriculumDataLoader = curriculumDataLoader;
		this.teacherAssignmentService = teacherAssignmentService;
		this.jsonDataLoader = jsonDataLoader;
	}

	public Map<String, Integer> validateSectionQuotas(List<Section> sections, List<SectionSubjectQuota> quotas) {

		if (sections == null || quotas == null) {
			throw new IllegalArgumentException("Sections or quotas data is missing.");
		}

		Map<String, Integer> sectionTotals = new HashMap<>();

		// Initialize totals only for sections having curriculum.
		for (SectionSubjectQuota quota : quotas) {

			if (quota == null) {
				throw new IllegalStateException("Null quota entry found.");
			}

			if (quota.getSectionId() == null || quota.getSectionId().isBlank()) {
				throw new IllegalStateException("Section ID is missing in quota.");
			}

			sectionTotals.putIfAbsent(quota.getSectionId(), 0);
		}

		// Validate and calculate weekly periods.
		for (SectionSubjectQuota quota : quotas) {

			if (quota.getSubjectName() == null || quota.getSubjectName().isBlank()) {
				throw new IllegalStateException("Subject name is missing for section: " + quota.getSectionId());
			}

			if (quota.getPeriodsPerWeek() < 0) {
				throw new IllegalStateException("Negative weekly quota found for subject: " + quota.getSubjectName());
			}

			if (quota.getPeriodsPerDay() < 0) {
				throw new IllegalStateException("Negative daily quota found for subject: " + quota.getSubjectName());
			}

			int currentTotal = sectionTotals.get(quota.getSectionId());

			sectionTotals.put(quota.getSectionId(), currentTotal + quota.getPeriodsPerWeek());
		}

		// Every mapped section must have exactly 52 periods.
		for (Map.Entry<String, Integer> entry : sectionTotals.entrySet()) {

			if (entry.getValue() != 52) {
				throw new IllegalStateException(
						"Section " + entry.getKey() + " has " + entry.getValue() + " weekly periods. Expected 52.");
			}
		}

		return sectionTotals;
	}

	public List<SectionSubjectQuota> findUnassignedQuotas() {

		SectionsData sectionsData = jsonDataLoader.loadSections();

		if (sectionsData == null || sectionsData.getSections() == null) {
			throw new IllegalStateException("Sections data is missing.");
		}

		List<Section> sections = sectionsData.getSections();

		CurriculumData curriculumData = curriculumDataLoader.loadCurriculum();

		List<SectionSubjectQuota> quotas = sectionQuotaBuilder.buildQuotas(sections, curriculumData);

		validateSectionQuotas(sections, quotas);

		return teacherAssignmentService.findUnassignedQuotas(quotas);
	}
}
