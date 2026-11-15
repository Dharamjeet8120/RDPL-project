
package rdpl_timetable_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rdpl_timetable_backend.dto.SectionCurriculumStatus;
import rdpl_timetable_backend.loader.CurriculumDataLoader;
import rdpl_timetable_backend.loader.JsonDataLoader;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.SectionSubjectQuota;
import rdpl_timetable_backend.service.QuotaValidationService;
import rdpl_timetable_backend.solver.SectionQuotaBuilder;

@RestController
@RequestMapping("/api/timetable")
public class QuotaController {

	private final JsonDataLoader jsonDataLoader;
	private final CurriculumDataLoader curriculumDataLoader;
	private final SectionQuotaBuilder sectionQuotaBuilder;
	private final QuotaValidationService quotaValidationService;

	public QuotaController(JsonDataLoader jsonDataLoader, CurriculumDataLoader curriculumDataLoader,
			SectionQuotaBuilder sectionQuotaBuilder, QuotaValidationService quotaValidationService) {

		this.jsonDataLoader = jsonDataLoader;
		this.curriculumDataLoader = curriculumDataLoader;
		this.sectionQuotaBuilder = sectionQuotaBuilder;
		this.quotaValidationService = quotaValidationService;
	}

	@GetMapping("/quotas")
	public List<SectionSubjectQuota> getQuotas() {

		List<Section> sections = jsonDataLoader.loadSections().getSections();

		CurriculumData curriculum = curriculumDataLoader.loadCurriculum();

		return sectionQuotaBuilder.buildQuotas(sections, curriculum);
	}

	@GetMapping("/quotas/validate")
	public Map<String, Integer> validateQuotas() {

		List<Section> sections = jsonDataLoader.loadSections().getSections();

		CurriculumData curriculum = curriculumDataLoader.loadCurriculum();

		List<SectionSubjectQuota> quotas = sectionQuotaBuilder.buildQuotas(sections, curriculum);

		return quotaValidationService.validateSectionQuotas(sections, quotas);
	}

	@GetMapping("/quotas/missing-sections")
	public List<Section> getMissingCurriculumSections() {

		List<Section> sections = jsonDataLoader.loadSections().getSections();

		CurriculumData curriculum = curriculumDataLoader.loadCurriculum();

		List<SectionSubjectQuota> quotas = sectionQuotaBuilder.buildQuotas(sections, curriculum);

		Set<String> mappedSectionIds = new HashSet<>();

		for (SectionSubjectQuota quota : quotas) {
			mappedSectionIds.add(quota.getSectionId());
		}

		List<Section> missingSections = new ArrayList<>();

		for (Section section : sections) {

			if (section == null || section.getId() == null) {
				continue;
			}

			if (!mappedSectionIds.contains(section.getId())) {
				missingSections.add(section);
			}
		}

		return missingSections;
	}

	@GetMapping("/quotas/mapping-report")
	public List<SectionCurriculumStatus> getCurriculumMappingReport() {

		List<Section> sections = jsonDataLoader.loadSections().getSections();

		CurriculumData curriculum = curriculumDataLoader.loadCurriculum();

		List<SectionSubjectQuota> quotas = sectionQuotaBuilder.buildQuotas(sections, curriculum);

		Map<String, Integer> weeklyTotals = new HashMap<>();

		for (SectionSubjectQuota quota : quotas) {

			weeklyTotals.merge(quota.getSectionId(), quota.getPeriodsPerWeek(), Integer::sum);
		}

		Set<String> mappedSectionIds = new HashSet<>(weeklyTotals.keySet());

		List<SectionCurriculumStatus> report = new ArrayList<>();

		for (Section section : sections) {

			if (section == null || section.getId() == null) {
				continue;
			}

			boolean mapped = mappedSectionIds.contains(section.getId());

			report.add(new SectionCurriculumStatus(section.getId(), section.getClassName(), section.getSectionName(),
					section.getCurriculumKey(), mapped ? "MAPPED" : "MISSING_CURRICULUM",
					mapped ? weeklyTotals.get(section.getId()) : null));
		}

		return report;
	}
}
