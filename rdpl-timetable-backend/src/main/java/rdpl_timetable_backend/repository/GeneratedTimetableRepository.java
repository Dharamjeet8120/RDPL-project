
package rdpl_timetable_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rdpl_timetable_backend.model.GeneratedTimetable;

@Repository
public interface GeneratedTimetableRepository extends JpaRepository<GeneratedTimetable, Long> {

	List<GeneratedTimetable> findAllByOrderByGeneratedAtDesc();

	List<GeneratedTimetable> findByAcademicYearOrderByGeneratedAtDesc(String academicYear);
}
