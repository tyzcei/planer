package by.bsuir.semesterpassport.domain.repository;

import by.bsuir.semesterpassport.domain.model.CourseTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseTeacherRepository extends JpaRepository<CourseTeacher, Long> {

    List<CourseTeacher> findByGroupNumber(String groupNumber);

    void deleteByGroupNumber(String groupNumber);

    // Обращаемся к объекту subject внутри CourseTeacher
    @Query("SELECT DISTINCT c.subject.title FROM CourseTeacher c WHERE c.groupNumber = :groupNumber")
    List<String> findDistinctSubjectsByGroupNumber(@Param("groupNumber") String groupNumber);

    // Spring Data JPA магия: ищем внутри связанных объектов через подчеркивание
    boolean existsByGroupNumberAndSubject_TitleAndTeacher_BsuirUrlId(String groupNumber, String subjectTitle, String bsuirUrlId);
}