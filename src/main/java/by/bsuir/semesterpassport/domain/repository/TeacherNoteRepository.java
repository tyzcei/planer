package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.TeacherNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeacherNoteRepository extends JpaRepository<TeacherNote, Long> {

    // Получить конкретную заметку
    Optional<TeacherNote> findByGroupNumberAndBsuirUrlId(String groupNumber, String bsuirUrlId);

    // Получить все заметки для группы
    List<TeacherNote> findByGroupNumber(String groupNumber);
}