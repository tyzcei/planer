package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.NoteRequest;
import by.bsuir.semesterpassport.application.dto.TeacherWithNoteDto;
import by.bsuir.semesterpassport.application.service.TeacherService;
import by.bsuir.semesterpassport.application.service.TeacherSyncService; // НОВЫЙ ИМПОРТ

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherSyncService teacherSyncService; // НОВОЕ ПОЛЕ

    // ОБНОВЛЕННЫЙ КОНСТРУКТОР
    public TeacherController(TeacherService teacherService, TeacherSyncService teacherSyncService) {
        this.teacherService = teacherService;
        this.teacherSyncService = teacherSyncService;
    }

    @GetMapping("/{groupNumber}")
    public ResponseEntity<List<TeacherWithNoteDto>> getGroupTeachers(@PathVariable String groupNumber) {
        return ResponseEntity.ok(teacherService.getTeachersWithNotes(groupNumber));
    }

    @PutMapping("/{groupNumber}/{bsuirUrlId}/note")
    @PreAuthorize("hasAuthority('GROUP_LEADER')")
    public ResponseEntity<Void> updateNote(
            @PathVariable String groupNumber,
            @PathVariable String bsuirUrlId,
            @RequestBody NoteRequest request) {
        teacherService.saveNote(groupNumber, bsuirUrlId, request.noteText());
        return ResponseEntity.ok().build();
    }

    // === НОВЫЙ ЭНДПОИНТ ДЛЯ АДМИНА ===
    @PostMapping("/force-sync")
    @PreAuthorize("hasAuthority('ADMIN')") // Строгая защита: только Админ!
    public ResponseEntity<String> forceSyncTeachers() {
        teacherSyncService.syncTeachersWeekly(); // Вызываем тот самый метод напрямую
        return ResponseEntity.ok("Синхронизация успешно выполнена!");
    }

    // Добавь этот метод в TeacherController.java:
    @PostMapping("/force-sync/{groupNumber}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> forceSyncTeachersForGroup(@PathVariable String groupNumber) {
        // Убедись, что этот метод в сервисе помечен как public!
        teacherSyncService.syncTeachersForGroup(groupNumber);
        return ResponseEntity.ok("Преподаватели для группы " + groupNumber + " обновлены!");
    }

    // ПОСМОТРЕТЬ СПИСОК ПРЕДМЕТОВ (Для выпадающего списка)
    @GetMapping("/{groupNumber}/subjects")
    public ResponseEntity<List<String>> getGroupSubjects(@PathVariable String groupNumber) {
        return ResponseEntity.ok(teacherService.getSubjectsForGroup(groupNumber));
    }
}