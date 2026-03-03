package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.LabWork;
import by.bsuir.semesterpassport.domain.model.LabStatus;
import by.bsuir.semesterpassport.domain.repository.LabWorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabWorkService {

    private final LabWorkRepository labWorkRepository;
    private final PrioritySorterService prioritySorterService;

    public LabWorkService(LabWorkRepository labWorkRepository, PrioritySorterService prioritySorterService) {
        this.labWorkRepository = labWorkRepository;
        this.prioritySorterService = prioritySorterService;
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
}