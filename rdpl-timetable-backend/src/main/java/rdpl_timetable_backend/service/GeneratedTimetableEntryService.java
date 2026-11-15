
package rdpl_timetable_backend.service;

import rdpl_timetable_backend.model.GeneratedTimetable;
import rdpl_timetable_backend.model.GeneratedTimetableEntry;
import rdpl_timetable_backend.repository.GeneratedTimetableEntryRepository;
import rdpl_timetable_backend.repository.GeneratedTimetableRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GeneratedTimetableEntryService {

	private final GeneratedTimetableEntryRepository entryRepository;
	private final GeneratedTimetableRepository timetableRepository;

	public GeneratedTimetableEntryService(GeneratedTimetableEntryRepository entryRepository,
			GeneratedTimetableRepository timetableRepository) {
		this.entryRepository = entryRepository;
		this.timetableRepository = timetableRepository;
	}

	public List<GeneratedTimetableEntry> saveEntries(Long timetableId, List<GeneratedTimetableEntry> entries) {

		if (timetableId == null) {
			throw new IllegalArgumentException("Timetable ID is required");
		}

		if (entries == null || entries.isEmpty()) {
			throw new IllegalArgumentException("Timetable entries cannot be empty");
		}

		GeneratedTimetable timetable = timetableRepository.findById(timetableId)
				.orElseThrow(() -> new IllegalArgumentException("Timetable not found: " + timetableId));

		for (GeneratedTimetableEntry entry : entries) {
			entry.setGeneratedTimetable(timetable);
		}

		return entryRepository.saveAll(entries);
	}

	@Transactional(readOnly = true)
	public List<GeneratedTimetableEntry> getEntries(Long timetableId) {

		if (!timetableRepository.existsById(timetableId)) {
			throw new IllegalArgumentException("Timetable not found: " + timetableId);
		}

		return entryRepository.findByGeneratedTimetable_Id(timetableId);
	}

	@Transactional(readOnly = true)
	public long countEntries(Long timetableId) {
		return entryRepository.countByGeneratedTimetable_Id(timetableId);
	}
}
