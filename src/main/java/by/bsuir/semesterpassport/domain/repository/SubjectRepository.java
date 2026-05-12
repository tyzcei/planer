package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Проверка существования конкретного предмета
    boolean existsByTitleAndGroupNumber(String title, String groupNumber);

    // Поиск конкретного предмета
    Optional<Subject> findByTitleAndGroupNumber(String title, String groupNumber);

    // ИСПРАВЛЕНО: добавили "All", чтобы сервис был счастлив!
    List<Subject> findAllByGroupNumber(String groupNumber);
}