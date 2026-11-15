
package rdpl_timetable_backend.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import rdpl_timetable_backend.model.ClassCurriculum;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.Section;

@Component
public class CurriculumSectionMapper {

	public Map<String, ClassCurriculum> mapSections(List<Section> sections, CurriculumData curriculumData) {

		if (sections == null || curriculumData == null || curriculumData.getClasses() == null) {

			throw new IllegalArgumentException("Sections or curriculum data is missing.");
		}

		Map<String, ClassCurriculum> curriculumByKey = new HashMap<>();

		Map<String, ClassCurriculum> curriculumByClass = new HashMap<>();

		// Prepare curriculum lookup maps.
		for (ClassCurriculum curriculum : curriculumData.getClasses()) {

			if (curriculum == null || curriculum.getClassName() == null) {
				continue;
			}

			String normalizedClassName = normalize(curriculum.getClassName());

			curriculumByClass.put(normalizedClassName, curriculum);

			// Supports class names such as Class 1, Class 2, etc.
			String classNumber = extractClassNumber(curriculum.getClassName());

			if (classNumber != null) {
				curriculumByKey.put("class-" + classNumber, curriculum);
			}
		}

		Map<String, ClassCurriculum> result = new HashMap<>();

		// Map sections using curriculumKey first.
		for (Section section : sections) {

			if (section == null || section.getId() == null) {
				continue;
			}

			ClassCurriculum matchedCurriculum = null;

			String curriculumKey = section.getCurriculumKey();

			if (curriculumKey != null && !curriculumKey.isBlank()) {

				matchedCurriculum = curriculumByKey.get(normalize(curriculumKey));
			}

			// Fallback for existing Class 1-12 data.
			if (matchedCurriculum == null) {

				String className = section.getClassName();

				if (className != null) {

					String classNumber = extractClassNumber(className);

					if (classNumber != null) {
						matchedCurriculum = curriculumByKey.get("class-" + classNumber);
					}

					if (matchedCurriculum == null) {
						matchedCurriculum = curriculumByClass.get(normalize(className));
					}
				}
			}

			if (matchedCurriculum != null) {
				result.put(section.getId(), matchedCurriculum);
			}
		}

		return result;
	}

	private String normalize(String value) {
		return value.trim().toLowerCase().replaceAll("\\s+", "-");
	}

	private String extractClassNumber(String className) {

		if (className == null || className.isBlank()) {
			return null;
		}

		String digits = className.replaceAll("\\D", "");

		return digits.isEmpty() ? null : digits;
	}
}
