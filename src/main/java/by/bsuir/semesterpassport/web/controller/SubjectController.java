package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.service.SubjectService;
import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@CrossOrigin("*")
public class SubjectController {
    private final SubjectService subjectService;
    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectService subjectService, SubjectRepository subjectRepository) {
        this.subjectService = subjectService;
        this.subjectRepository=subjectRepository;
    }

    @GetMapping("/group/{groupNumber}")
    public ResponseEntity<List<Subject>> getSubjectsByGroup(@PathVariable String groupNumber) {
        // ИСПРАВЛЕНО: Теперь вызываем findAllByGroupNumber, так как мы переименовали его в репозитории
        return ResponseEntity.ok(subjectRepository.findAllByGroupNumber(groupNumber));
    }
}