package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.CourseTeacher;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseTeacherRepository extends JpaRepository<CourseTeacher, Long> {

    // Для фронтенда: получить всех преподов группы
    List<CourseTeacher> findByGroupNumber(String groupNumber);

    // Для синхронизатора: очистить старых преподов
    void deleteByGroupNumber(String groupNumber);
    // Получить список уникальных предметов для группы
    @Query("SELECT DISTINCT c.subjectTitle FROM CourseTeacher c WHERE c.groupNumber = :groupNumber")
    List<String> findDistinctSubjectsByGroupNumber(@Param("groupNumber") String groupNumber);

    // Для синхронизатора: проверка на дубликаты
    boolean existsByGroupNumberAndSubjectTitleAndBsuirUrlId(String groupNumber, String subjectTitle, String bsuirUrlId);
}