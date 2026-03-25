package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.LabWorkDisplayDTO;
import by.bsuir.semesterpassport.application.dto.LabWorkRequest;
import by.bsuir.semesterpassport.application.service.LabWorkService;
import by.bsuir.semesterpassport.application.service.PrioritySorterService;
import by.bsuir.semesterpassport.domain.model.LabWork;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/labs")
@CrossOrigin("*") // Разрешаем запросы с фронтенда
public class LabWorkController {

    private final LabWorkService labWorkService;
    private final PrioritySorterService prioritySorterService;

    public LabWorkController(LabWorkService labWorkService, PrioritySorterService prioritySorterService) {
        this.labWorkService = labWorkService;
        this.prioritySorterService = prioritySorterService;
    }

    // Получение списка для дашборда (с сортировкой)
    @GetMapping("/dashboard")
    public ResponseEntity<List<LabWorkDisplayDTO>> getDashboard(@RequestParam Long userId) {
        List<LabWork> sortedLabs = labWorkService.getStudentLabsSorted(userId);
        List<LabWorkDisplayDTO> response = sortedLabs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Создание новой лабы
    @PostMapping
    public ResponseEntity<LabWorkDisplayDTO> createLab(@RequestBody LabWorkRequest request) {
        // Сначала создаем лабу в сервисе
        LabWork createdLab = labWorkService.createLab(request);
        // Сразу превращаем её в DTO и отправляем фронтенду
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdLab));
    }

    // Переключение статуса (Клик по кнопке на карточке)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<LabWorkDisplayDTO> toggleStatus(@PathVariable Long id, @RequestParam Long userId) {
        LabWork updated = labWorkService.toggleStatus(id, userId);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    // Удаление лабы
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLab(@PathVariable Long id) {
        labWorkService.deleteLab(id);
        return ResponseEntity.noContent().build();
    }

    // Редактирование (Обновление данных)
    @PutMapping("/{id}")
    public ResponseEntity<LabWorkDisplayDTO> updateLab(@PathVariable Long id, @RequestBody LabWorkRequest request) {
        // Обновляем лабу
        LabWork updatedLab = labWorkService.updateLab(id, request);
        // Возвращаем DTO вместо "грязной" сущности LabWork
        return ResponseEntity.ok(convertToDTO(updatedLab));
    }

    /**
     * Вспомогательный метод для превращения модели в DTO.
     * ВАЖНО: Порядок аргументов должен строго совпадать с конструктором LabWorkDisplayDTO!
     */
    private LabWorkDisplayDTO convertToDTO(LabWork lab) {
        return new LabWorkDisplayDTO(
                lab.getLabId(),
                lab.getTitle(),
                lab.getSubject() != null ? lab.getSubject().getTitle() : "Предмет не указан",
                lab.getSubject() != null ? lab.getSubject().getSubjectId() : null, // ДОБАВИЛИ ID ПРЕДМЕТА
                lab.getPractitioner() != null ? lab.getPractitioner().getFullName() : "Не назначен",
                lab.getComplexity(),
                lab.getDeadline(),
                lab.getCurrentStatus().name(), // Это поле на фронте ловится как 'status'
                prioritySorterService.calculateScore(lab)
        );
    }
}