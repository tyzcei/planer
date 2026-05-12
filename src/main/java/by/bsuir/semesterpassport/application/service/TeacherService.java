package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.TeacherWithNoteDto;
import by.bsuir.semesterpassport.domain.model.CourseTeacher;
import by.bsuir.semesterpassport.domain.model.Teacher;
import by.bsuir.semesterpassport.domain.model.TeacherNote;
import by.bsuir.semesterpassport.domain.repository.CourseTeacherRepository;
import by.bsuir.semesterpassport.domain.repository.TeacherNoteRepository;
import by.bsuir.semesterpassport.domain.repository.TeacherRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final CourseTeacherRepository courseTeacherRepository;
    private final TeacherNoteRepository noteRepository;
    private final TeacherRepository teacherRepository;

    public TeacherService(CourseTeacherRepository courseTeacherRepository,
                          TeacherNoteRepository noteRepository,
                          TeacherRepository teacherRepository) {
        this.courseTeacherRepository = courseTeacherRepository;
        this.noteRepository = noteRepository;
        this.teacherRepository = teacherRepository;
    }

    @Cacheable(value = "scheduleCache", key = "#groupNumber")
    public List<TeacherWithNoteDto> getTeachersWithNotes(String groupNumber) {
        List<CourseTeacher> teachers = courseTeacherRepository.findByGroupNumber(groupNumber);

        Map<String, String> notesMap = noteRepository.findByGroupNumber(groupNumber).stream()
                .collect(Collectors.toMap(
                        note -> note.getTeacher().getBsuirUrlId(), // Берем ID из объекта Teacher
                        TeacherNote::getNoteText
                ));

        return teachers.stream()
                .map(t -> new TeacherWithNoteDto(
                        t.getSubject().getTitle(),
                        t.getTeacher().getFullName(),
                        t.getTeacher().getBsuirUrlId(),
                        t.getLessonType(),
                        t.getTeacher().getPhotoLink(),
                        notesMap.get(t.getTeacher().getBsuirUrlId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "scheduleCache", key = "#groupNumber")
    public void saveNote(String groupNumber, String bsuirUrlId, String text) {
        // Находим преподавателя в новой базе
        Teacher teacher = teacherRepository.findByBsuirUrlId(bsuirUrlId)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден в БД"));

        TeacherNote note = noteRepository.findByGroupNumberAndTeacher_BsuirUrlId(groupNumber, bsuirUrlId)
                .orElse(new TeacherNote(groupNumber, teacher, text));

        note.setNoteText(text);
        noteRepository.save(note);
    }

    public List<String> getSubjectsForGroup(String groupNumber) {
        return courseTeacherRepository.findDistinctSubjectsByGroupNumber(groupNumber);
    }
}