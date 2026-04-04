package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    boolean existsByTitle(String title);
    boolean existsByTitleAndGroupNumber(String title, String groupNumber);

    List<Subject> findByGroupNumber(String groupNumber);

    @Query(value = "SELECT s.* FROM subjects s " +
            "JOIN group_subjects gs ON s.subject_id = gs.subject_id " +
            "WHERE gs.group_number = :groupNumber", nativeQuery = true)
    List<Subject> findAllByGroupNumber(@Param("groupNumber") String groupNumber);
}