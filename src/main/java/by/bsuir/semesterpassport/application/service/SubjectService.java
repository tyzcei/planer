package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.Subject;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getSubjectsByGroup(String groupNumber) {
        return subjectRepository.findAllByGroupNumber(groupNumber);
    }
}