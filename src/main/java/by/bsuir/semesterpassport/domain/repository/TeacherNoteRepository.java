package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.TeacherNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeacherNoteRepository extends JpaRepository<TeacherNote, Long> {

    // Ищем заметку по группе и bsuirUrlId связанного преподавателя
    Optional<TeacherNote> findByGroupNumberAndTeacher_BsuirUrlId(String groupNumber, String bsuirUrlId);

    List<TeacherNote> findByGroupNumber(String groupNumber);
}