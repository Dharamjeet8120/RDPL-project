package rdpl_timetable_backend.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;
import rdpl_timetable_backend.model.SectionMetadata;

public interface SectionMetadataRepository extends JpaRepository<SectionMetadata, String> {
}
