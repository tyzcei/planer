package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.LabWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabWorkRepository extends JpaRepository<LabWork, Long> {

    // Поиск всех лаб конкретного студента
    List<LabWork> findAllByUserUserId(Long userId);

    // Использование Optional для безопасной обработки null (требование ТЗ)
    Optional<LabWork> findByLabIdAndUserUserId(Long labId, Long userId);
}