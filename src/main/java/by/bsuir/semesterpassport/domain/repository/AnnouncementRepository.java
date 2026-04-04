package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Optional<Announcement> findByGroupNumber(String groupNumber);
}