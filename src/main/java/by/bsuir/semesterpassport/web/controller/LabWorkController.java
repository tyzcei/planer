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
@CrossOrigin("*")
public class LabWorkController {

    private final LabWorkService labWorkService;
    // ИСПРАВЛЕНО: Тип должен быть PrioritySorterService (с большой буквы)
    private final PrioritySorterService prioritySorterService;

    // Ручной конструктор
    public LabWorkController(LabWorkService labWorkService, PrioritySorterService prioritySorterService) {
        this.labWorkService = labWorkService;
        this.prioritySorterService = prioritySorterService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<LabWorkDisplayDTO>> getDashboard(@RequestParam Long userId) {
        List<LabWork> sortedLabs = labWorkService.getStudentLabsSorted(userId);

        List<LabWorkDisplayDTO> response = sortedLabs.stream()
                .map(lab -> new LabWorkDisplayDTO(
                        lab.getLabId(),
                        lab.getTitle(),
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

    @PostMapping
    public ResponseEntity<LabWork> createLab(@RequestBody LabWorkRequest request) {
        LabWork createdLab = labWorkService.createLab(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLab);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<LabWorkDisplayDTO> toggleStatus(@PathVariable Long id, @RequestParam Long userId) {
        LabWork updated = labWorkService.toggleStatus(id, userId);

        LabWorkDisplayDTO dto = new LabWorkDisplayDTO(
                updated.getLabId(),
                updated.getTitle(),
                updated.getSubject() != null ? updated.getSubject().getTitle() : "Предмет не указан",
                "Не назначен",
                updated.getComplexity(),
                updated.getDeadline(),
                updated.getCurrentStatus().name(),
                prioritySorterService.calculateScore(updated)
        );

        return ResponseEntity.ok(dto);
    }
}