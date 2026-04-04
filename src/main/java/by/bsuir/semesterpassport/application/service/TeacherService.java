package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.TeacherWithNoteDto;
import by.bsuir.semesterpassport.domain.model.CourseTeacher;
import by.bsuir.semesterpassport.domain.model.TeacherNote;
import by.bsuir.semesterpassport.domain.repository.CourseTeacherRepository;
import by.bsuir.semesterpassport.domain.repository.TeacherNoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final CourseTeacherRepository teacherRepository;
    private final TeacherNoteRepository noteRepository;

    public TeacherService(CourseTeacherRepository teacherRepository, TeacherNoteRepository noteRepository) {
        this.teacherRepository = teacherRepository;
        this.noteRepository = noteRepository;
    }

    // Собираем Франкенштейна: Преподаватель + Заметка
    public List<TeacherWithNoteDto> getTeachersWithNotes(String groupNumber) {
        List<CourseTeacher> teachers = teacherRepository.findByGroupNumber(groupNumber);

        // Превращаем список заметок в словарь (urlId -> заметка) для быстрого поиска
        Map<String, String> notesMap = noteRepository.findByGroupNumber(groupNumber).stream()
                .collect(Collectors.toMap(TeacherNote::getBsuirUrlId, TeacherNote::getNoteText));

        return teachers.stream()
                .map(t -> new TeacherWithNoteDto(
                        t.getSubjectTitle(),
                        t.getTeacherName(),
                        t.getBsuirUrlId(),
                        t.getLessonType(),
                        t.getPhotoLink(),
                        notesMap.get(t.getBsuirUrlId()) // Подтягиваем заметку, если она есть
                ))
                .collect(Collectors.toList());
    }

    // Сохранить или обновить заметку (Только для старосты)
    @Transactional
    public void saveNote(String groupNumber, String bsuirUrlId, String text) {
        TeacherNote note = noteRepository.findByGroupNumberAndBsuirUrlId(groupNumber, bsuirUrlId)
                .orElse(new TeacherNote(groupNumber, bsuirUrlId, text));

        note.setNoteText(text);
        noteRepository.save(note);
    }

    // Получить только названия предметов
    public List<String> getSubjectsForGroup(String groupNumber) {
        return teacherRepository.findDistinctSubjectsByGroupNumber(groupNumber);
    }
}