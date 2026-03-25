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

    public LabWorkService(LabWorkRepository labWorkRepository,
                          PrioritySorterService prioritySorterService,
                          UserRepository userRepository,
                          SubjectRepository subjectRepository) {
        this.labWorkRepository = labWorkRepository;
        this.prioritySorterService = prioritySorterService;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
    }

    // 1. Получение отсортированного списка для Dashboard
    public List<LabWork> getStudentLabsSorted(Long userId) {
        List<LabWork> labs = labWorkRepository.findAllByUserUserId(userId);
        return prioritySorterService.sortLabsByPriority(labs);
    }

    // 2. Создание новой лабы
    @Transactional
    public LabWork createLab(LabWorkRequest request) {
        LabWork lab = new LabWork();
        lab.setTitle(request.getTitle());
        lab.setComplexity(request.getComplexity());

        lab.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Студент не найден")));

        lab.setSubject(subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Предмет не найден")));

        lab.setDeadline(request.getDeadline() != null ? request.getDeadline() : LocalDateTime.now().plusWeeks(2));
        lab.setCurrentStatus(LabStatus.RECEIVED);

        return labWorkRepository.save(lab);
    }

    // 3. Редактирование существующей лабы
    @Transactional
    public LabWork updateLab(Long id, LabWorkRequest request) {
        LabWork lab = labWorkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Лабораторная работа не найдена"));

        lab.setTitle(request.getTitle());
        lab.setComplexity(request.getComplexity());
        lab.setDeadline(request.getDeadline());

        if (request.getSubjectId() != null) {
            lab.setSubject(subjectRepository.findById(request.getSubjectId())
                    .orElse(lab.getSubject()));
        }

        return labWorkRepository.save(lab);
    }

    // 4. УДАЛЕНИЕ (ОСТАВЛЯЕМ ТОЛЬКО ОДИН РАЗ)
    @Transactional
    public void deleteLab(Long id) {
        labWorkRepository.deleteById(id);
    }

    // 5. Переключение статуса по кругу
    @Transactional
    public LabWork toggleStatus(Long labId, Long userId) {
        LabWork lab = labWorkRepository.findByLabIdAndUserUserId(labId, userId)
                .orElseThrow(() -> new RuntimeException("Лабораторная работа не найдена"));

        LabStatus nextStatus = switch (lab.getCurrentStatus()) {
            case RECEIVED -> LabStatus.CODED;
            case CODED -> LabStatus.READY;
            case READY -> LabStatus.SUBMITTED;
            case SUBMITTED -> LabStatus.PROTECTED;
            case PROTECTED -> LabStatus.RECEIVED;
        };

        lab.setCurrentStatus(nextStatus);
        return labWorkRepository.save(lab);
    }
}