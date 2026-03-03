package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Позволяет сисадмину проверять наличие предмета по названию (UC-2)
    boolean existsByTitle(String title);
}