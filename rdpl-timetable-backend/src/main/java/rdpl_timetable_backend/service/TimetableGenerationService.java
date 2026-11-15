
package rdpl_timetable_backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rdpl_timetable_backend.loader.CurriculumDataLoader;
import rdpl_timetable_backend.loader.JsonDataLoader;
import rdpl_timetable_backend.model.CurriculumData;
import rdpl_timetable_backend.model.GeneratedTimetable;
import rdpl_timetable_backend.model.GeneratedTimetableEntry;
import rdpl_timetable_backend.model.Section;
import rdpl_timetable_backend.model.SectionSubjectQuota;
import rdpl_timetable_backend.model.SectionsData;
import rdpl_timetable_backend.model.TimeSlot;
import rdpl_timetable_backend.model.TimetableEntry;
import rdpl_timetable_backend.solver.GlobalTimetableSolver;
import rdpl_timetable_backend.solver.SectionQuotaBuilder;
import rdpl_timetable_backend.solver.SolverResult;

@Service
public class TimetableGenerationService {

	private static final Logger log = LoggerFactory.getLogger(TimetableGenerationService.class);

	private static final String ACADEMIC_YEAR = "2026-2027";

	private final JsonDataLoader jsonDataLoader;
	private final CurriculumDataLoader curriculumDataLoader;
	private final SectionQuotaBuilder sectionQuotaBuilder;
	private final QuotaValidationService quotaValidationService;
	private final GlobalTimetableSolver globalTimetableSolver;
	private final GeneratedTimetableService generatedTimetableService;
	private final GeneratedTimetableEntryService generatedTimetableEntryService;
	private final List<TimeSlot> timeSlots;

	public TimetableGenerationService(JsonDataLoader jsonDataLoader, CurriculumDataLoader curriculumDataLoader,
			SectionQuotaBuilder sectionQuotaBuilder, QuotaValidationService quotaValidationService,
			GlobalTimetableSolver globalTimetableSolver, GeneratedTimetableService generatedTimetableService,
			GeneratedTimetableEntryService generatedTimetableEntryService,
			@Qualifier("timeSlots") List<TimeSlot> timeSlots) {

		this.jsonDataLoader = jsonDataLoader;
		this.curriculumDataLoader = curriculumDataLoader;
		this.sectionQuotaBuilder = sectionQuotaBuilder;
		this.quotaValidationService = quotaValidationService;
		this.globalTimetableSolver = globalTimetableSolver;
		this.generatedTimetableService = generatedTimetableService;
		this.generatedTimetableEntryService = generatedTimetableEntryService;
		this.timeSlots = timeSlots;
	}

	@Transactional
	public SolverResult generateTimetable() {

		log.info("========== TIMETABLE GENERATION STARTED ==========");

		// 1. Load sections.
		SectionsData sectionsData = jsonDataLoader.loadSections();

		if (sectionsData == null || sectionsData.getSections() == null || sectionsData.getSections().isEmpty()) {

			throw new IllegalStateException("Sections data is missing.");
		}

		List<Section> sections = sectionsData.getSections();

		log.info("Total Sections: {}", sections.size());

		// 2. Load curriculum.
		CurriculumData curriculumData = curriculumDataLoader.loadCurriculum();

		if (curriculumData == null) {
			throw new IllegalStateException("Curriculum data is missing.");
		}

		// 3. Generate section quotas.
		List<SectionSubjectQuota> quotas = sectionQuotaBuilder.buildQuotas(sections, curriculumData);

		if (quotas == null || quotas.isEmpty()) {
			throw new IllegalStateException("No section quotas found.");
		}

		log.info("Total Section Quotas: {}", quotas.size());

		// 4. Validate section quotas.
		quotaValidationService.validateSectionQuotas(sections, quotas);

		// 5. Validate weekly time slots.
		validateTimeSlots();

		// 6. Generate timetable using CP-SAT solver.
		log.info("========== CALLING CP-SAT SOLVER ==========");

		SolverResult result = globalTimetableSolver.solve(quotas, timeSlots);

		if (result == null) {
			throw new IllegalStateException("Solver returned an empty result.");
		}

		log.info("Solver Status: {}", result.getStatus());

		// 7. Save only successful timetables.
		String status = result.getStatus();

		if (!"OPTIMAL".equalsIgnoreCase(status) && !"FEASIBLE".equalsIgnoreCase(status)) {

			log.warn("Timetable not generated. Solver status: {}", status);

			return result;
		}

		List<TimetableEntry> timetable = result.getTimetable();

		if (timetable == null || timetable.isEmpty()) {
			throw new IllegalStateException("Solver succeeded but returned no timetable entries.");
		}

		// 8. Create generated timetable history.
		GeneratedTimetable savedTimetable = generatedTimetableService.save(ACADEMIC_YEAR, status);

		// 9. Convert solver entries into database entities.
		List<GeneratedTimetableEntry> entries = new ArrayList<>();

		for (TimetableEntry item : timetable) {

			if (item == null) {
				continue;
			}

			GeneratedTimetableEntry entry = new GeneratedTimetableEntry();

			entry.setSectionId(item.getSectionId());
			entry.setSubjectName(item.getSubjectName());
			entry.setTeacherId(item.getTeacherId());
			entry.setType(item.getType());
			entry.setDay(item.getDay());
			entry.setPeriodNumber(item.getPeriodNumber());

			entries.add(entry);
		}

		if (entries.isEmpty()) {
			throw new IllegalStateException("No valid timetable entries available to save.");
		}

		// 10. Save timetable entries.
		generatedTimetableEntryService.saveEntries(savedTimetable.getId(), entries);

		log.info("========== TIMETABLE DATABASE SAVE ==========");
		log.info("History ID: {}", savedTimetable.getId());
		log.info("Academic Year: {}", ACADEMIC_YEAR);
		log.info("Status: {}", status);
		log.info("Saved Entries: {}", entries.size());
		log.info("==============================================");

		log.info("========== TIMETABLE GENERATION COMPLETED ==========");

		return result;
	}

	// --------------------------------------------------
	// TIME SLOT VALIDATION
	// --------------------------------------------------

	private void validateTimeSlots() {

		if (timeSlots == null || timeSlots.isEmpty()) {
			throw new IllegalStateException("Time slots are missing.");
		}

		Set<String> uniqueSlots = new HashSet<>();

		log.info("========== TIME SLOT VALIDATION ==========");

		for (TimeSlot slot : timeSlots) {

			if (slot == null) {
				throw new IllegalStateException("Null time slot detected.");
			}

			if (slot.getDay() == null || slot.getDay().isBlank()) {

				throw new IllegalStateException("Time slot day is missing.");
			}

			String key = slot.getDay().trim().toUpperCase() + "-" + slot.getPeriodNumber();

			if (!uniqueSlots.add(key)) {
				throw new IllegalStateException("Duplicate time slot detected: " + key);
			}
		}

		log.info("Total Time Slots: {}", timeSlots.size());
		log.info("Unique Time Slots: {}", uniqueSlots.size());

		if (timeSlots.size() != 52) {
			log.warn("Expected 52 slots (Mon-Thu 8, Fri-Sat 10 periods), but found {}.", timeSlots.size());
		}

		log.info("==========================================");
	}
}
