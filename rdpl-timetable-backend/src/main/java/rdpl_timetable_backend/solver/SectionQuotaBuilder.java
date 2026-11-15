
package rdpl_timetable_backend.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.ClassCurriculum;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.CurriculumItem;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.SectionSubjectQuota;

@Component
public class SectionQuotaBuilder {

	private final CurriculumSectionMapper curriculumSectionMapper;

	public SectionQuotaBuilder(CurriculumSectionMapper curriculumSectionMapper) {

		this.curriculumSectionMapper = curriculumSectionMapper;
	}

	public List<SectionSubjectQuota> buildQuotas(List<Section> sections, CurriculumData curriculumData) {

		if (sections == null || sections.isEmpty()) {
			throw new IllegalArgumentException("Section data is missing.");
		}

		if (curriculumData == null || curriculumData.getClasses() == null || curriculumData.getClasses().isEmpty()) {

			throw new IllegalArgumentException("Curriculum data is missing.");
		}

		Map<String, ClassCurriculum> sectionCurriculumMap = curriculumSectionMapper.mapSections(sections,
				curriculumData);

		List<SectionSubjectQuota> quotas = new ArrayList<>();

		for (Section section : sections) {

			if (section == null || section.getId() == null) {
				continue;
			}

			ClassCurriculum curriculum = sectionCurriculumMap.get(section.getId());

			// Pre-primary sections without curriculum are skipped.
			if (curriculum == null) {
				System.out.println(
						"No curriculum available for section: " + section.getId() + " - " + section.getClassName());
				continue;
			}

			if (curriculum.getItems() == null || curriculum.getItems().isEmpty()) {

				throw new IllegalStateException("Curriculum items are missing for section: " + section.getId());
			}

			for (CurriculumItem item : curriculum.getItems()) {

				if (item == null) {
					continue;
				}

				if (item.getName() == null || item.getName().isBlank()) {

					throw new IllegalStateException("Subject name is missing for section: " + section.getId());
				}

				if (item.getPeriodsPerWeek() < 0) {
					throw new IllegalStateException("Invalid weekly quota for subject: " + item.getName());
				}

				SectionSubjectQuota quota = new SectionSubjectQuota(section.getId(), section.getClassName(),
						item.getName(), item.getType(), item.getPeriodsPerDay(), item.getPeriodsPerWeek());

				quotas.add(quota);
			}
		}

		return quotas;
	}
}
