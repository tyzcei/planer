package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.service.SubjectService;
import by.bsuir.semesterpassport.domain.model.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@CrossOrigin("*")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/group/{groupNumber}")
    public ResponseEntity<List<Subject>> getSubjectsByGroup(@PathVariable String groupNumber) {
        return ResponseEntity.ok(subjectService.getSubjectsByGroup(groupNumber));
    }
}