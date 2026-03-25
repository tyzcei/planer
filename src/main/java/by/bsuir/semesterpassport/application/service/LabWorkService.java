package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.application.dto.LabWorkRequest;
import by.bsuir.semesterpassport.domain.model.LabWork;
import by.bsuir.semesterpassport.domain.model.LabStatus;
import by.bsuir.semesterpassport.domain.repository.LabWorkRepository;
import by.bsuir.semesterpassport.domain.repository.SubjectRepository;
import by.bsuir.semesterpassport.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LabWorkService {

    private final LabWorkRepository labWorkRepository;
    private final PrioritySorterService prioritySorterService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public LabWorkService(LabWorkRepository labWorkRepository, PrioritySorterService prioritySorterService, UserRepository userRepository, SubjectRepository subjectRepository) {
        this.labWorkRepository = labWorkRepository;
        this.prioritySorterService = prioritySorterService;
        this.userRepository=userRepository;
        this.subjectRepository=subjectRepository;
    }

    // UC-11: Получение отсортированного списка работ для Dashboard
    public List<LabWork> getStudentLabsSorted(Long userId) {
        List<LabWork> labs = labWorkRepository.findAllByUserUserId(userId);
        return prioritySorterService.sortLabsByPriority(labs);
    }

    // UC-9: Смена статуса (Жизненный цикл лабы)
    @Transactional
    public LabWork updateLabStatus(Long labId, Long userId, LabStatus newStatus) {
        return labWorkRepository.findByLabIdAndUserUserId(labId, userId)
                .map(lab -> {
                    lab.setCurrentStatus(newStatus);
                    return labWorkRepository.save(lab);
                })
                .orElseThrow(() -> new RuntimeException("Lab work not found for this student"));
    }

    @Transactional
    public LabWork createLab(LabWorkRequest request) {
        LabWork lab = new LabWork();
        lab.setTitle(request.title());
        lab.setComplexity(request.complexity());

        // Привязываем студента (тебя)
        lab.setUser(userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Студент не найден")));

        // Привязываем предмет (например, РИС)
        lab.setSubject(subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден")));

        // Если дедлайн не пришел, ставим через 14 дней от сегодня
        lab.setDeadline(request.deadline() != null ? request.deadline() : LocalDateTime.now().plusWeeks(2));

        return labWorkRepository.save(lab);
    }

    // В LabWorkService.java

    @Transactional
    public LabWork toggleStatus(Long labId, Long userId) {
        LabWork lab = labWorkRepository.findByLabIdAndUserUserId(labId, userId)
                .orElseThrow(() -> new RuntimeException("Лаба не найдена"));

        LabStatus current = lab.getCurrentStatus();
        LabStatus next = switch (current) {
            case RECEIVED -> LabStatus.CODED;
            case CODED -> LabStatus.READY;
            case READY -> LabStatus.SUBMITTED;
            case SUBMITTED -> LabStatus.PROTECTED;
            case PROTECTED -> LabStatus.RECEIVED; // Цикл замыкается
        };

        lab.setCurrentStatus(next);
        return labWorkRepository.save(lab);
    }
}