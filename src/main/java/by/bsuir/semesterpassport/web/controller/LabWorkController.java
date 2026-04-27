package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.LabWorkDisplayDTO;
import by.bsuir.semesterpassport.application.dto.LabWorkRequest;
import by.bsuir.semesterpassport.application.service.LabWorkService;
import by.bsuir.semesterpassport.application.service.PrioritySorterService;
import by.bsuir.semesterpassport.domain.model.LabWork;
import by.bsuir.semesterpassport.domain.model.User;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/labs")
@CrossOrigin("*")
public class LabWorkController {

    private final LabWorkService labWorkService;
    private final PrioritySorterService prioritySorterService;
    private final UserRepository userRepository;

    public LabWorkController(LabWorkService labWorkService, PrioritySorterService prioritySorterService, UserRepository userRepository) {
        this.labWorkService = labWorkService;
        this.prioritySorterService = prioritySorterService;
        this.userRepository=userRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<LabWorkDisplayDTO>> getDashboard(@RequestParam Long userId) {
        List<LabWork> sortedLabs = labWorkService.getStudentLabsSorted(userId);
        List<LabWorkDisplayDTO> response = sortedLabs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LabWorkDisplayDTO> createLab(@RequestBody LabWorkRequest request) {
        LabWork createdLab = labWorkService.createLab(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdLab));
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<LabWorkDisplayDTO> toggleStatus(@PathVariable Long id, @RequestParam Long userId) {
        LabWork updated = labWorkService.toggleStatus(id, userId);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLab(@PathVariable Long id) {
        labWorkService.deleteLab(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabWorkDisplayDTO> updateLab(@PathVariable Long id, @RequestBody LabWorkRequest request) {
        LabWork updatedLab = labWorkService.updateLab(id, request);
        return ResponseEntity.ok(convertToDTO(updatedLab));
    }

    private LabWorkDisplayDTO convertToDTO(LabWork lab) {
        return new LabWorkDisplayDTO(
                lab.getLabId(),
                lab.getTitle(),
                lab.getSubject() != null ? lab.getSubject().getTitle() : "Предмет не указан",
                lab.getSubject() != null ? lab.getSubject().getSubjectId() : null,
                lab.getPractitioner() != null ? lab.getPractitioner().getFullName() : "Не назначен",
                lab.getComplexity(),
                lab.getDeadline(),
                lab.getCurrentStatus().name(),
                prioritySorterService.calculateScore(lab)
        );
    }

    @PostMapping("/group-broadcast")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GROUP_LEADER')")
    public ResponseEntity<String> broadcastLab(@RequestBody LabWorkRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        by.bsuir.semesterpassport.domain.model.User domainUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден в БД"));

        labWorkService.broadcastLabToGroup(request, domainUser.getGroupNumber());

        return ResponseEntity.ok("Лабораторная успешно добавлена всей группе " + domainUser.getGroupNumber());
    }

    // ==========================================
    // НОВЫЙ ЭНДПОИНТ ДЛЯ СТАТИСТИКИ ФРОНТЕНДА
    // ==========================================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics(@RequestParam Long userId) {
        return ResponseEntity.ok(labWorkService.getStatistics(userId));
    }
}