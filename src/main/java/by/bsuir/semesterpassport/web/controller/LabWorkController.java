package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.LabWorkDisplayDTO;
import by.bsuir.semesterpassport.application.service.LabWorkService;
import by.bsuir.semesterpassport.application.service.PrioritySorterService;
import by.bsuir.semesterpassport.domain.model.LabWork;
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
                        lab.getSubject().getTitle(),
                        lab.getPractitioner().getFullName(),
                        lab.getComplexity(),
                        lab.getDeadline(),
                        lab.getCurrentStatus().name(),
                        prioritySorterService.calculateScore(lab) // UC-9: Расчет P = S + T
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}