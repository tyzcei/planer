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
public class LabWorkController {

    private final LabWorkService labWorkService;
    private final PrioritySorterService prioritySorterService;

    public LabWorkController(LabWorkService labWorkService, PrioritySorterService prioritySorterService) {
        this.labWorkService = labWorkService;
        this.prioritySorterService = prioritySorterService;
    }

    // UC-11: Получение списка приоритетных задач для Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<List<LabWorkDisplayDTO>> getDashboard(@RequestParam Long userId) {
        List<LabWork> sortedLabs = labWorkService.getStudentLabsSorted(userId);

        List<LabWorkDisplayDTO> response = sortedLabs.stream()
                .map(lab -> new LabWorkDisplayDTO(
                        lab.getLabId(),
                        lab.getTitle(),
                        // Безопасная проверка на null:
                        lab.getSubject() != null ? lab.getSubject().getTitle() : "Предмет не указан",
                        lab.getPractitioner() != null ? lab.getPractitioner().getFullName() : "Не назначен",
                        lab.getComplexity(),
                        lab.getDeadline(),
                        lab.getCurrentStatus().name(),
                        prioritySorterService.calculateScore(lab)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Добавь в LabWorkController.java

    // Добавь в LabWorkController.java

    @PostMapping
    public ResponseEntity<LabWork> createLab(@RequestBody LabWorkRequest request) {
        // Вызываем сервис для сохранения лабы
        LabWork createdLab = labWorkService.createLab(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLab);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<LabWorkDisplayDTO> toggleStatus(@PathVariable Long id, @RequestParam Long userId) {
        LabWork updated = labWorkService.toggleStatus(id, userId);

        // Возвращаем DTO, чтобы фронтенд сразу увидел новый статус и приоритет
        LabWorkDisplayDTO dto = new LabWorkDisplayDTO(
                updated.getLabId(),
                updated.getTitle(),
                updated.getSubject().getTitle(),
                updated.getPractitioner() != null ? updated.getPractitioner().getFullName() : "Не назначен",
                updated.getComplexity(),
                updated.getDeadline(),
                updated.getCurrentStatus().name(),
                prioritySorterService.calculateScore(updated)
        );

        return ResponseEntity.ok(dto);
    }
}