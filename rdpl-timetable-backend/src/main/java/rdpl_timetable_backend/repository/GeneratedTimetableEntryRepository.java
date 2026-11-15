
package rdpl_timetable_backend.repository;

import rdpl_timetable_backend.model.GeneratedTimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GeneratedTimetableEntryRepository extends JpaRepository<GeneratedTimetableEntry, Long> {

	List<GeneratedTimetableEntry> findByGeneratedTimetable_Id(Long timetableId);

	long countByGeneratedTimetable_Id(Long timetableId);
}
