package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.LabWork;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;

@Service
public class PrioritySorterService {

    public PrioritySorterService() {}

    /**
     * UC-11: Сортирует список лабораторных работ по приоритету. [cite: 376]
     */
    public List<LabWork> sortLabsByPriority(List<LabWork> labs) {
        if (labs == null) return List.of();

        return labs.stream()
                .sorted(Comparator.comparingDouble(this::calculateScore).reversed())
                .toList();
    }

    /**
     * Бизнес-правило BR-3: P = S + T. [cite: 357]
     */
    public double calculateScore(LabWork lab) {
        // S - Сложность (1-5) [cite: 357]
        int s = (lab.getComplexity() != null) ? lab.getComplexity() : 1;

        // T - Временной фактор до дедлайна [cite: 357]
        double t = calculateTimeFactor(lab.getDeadline());

        return s + t;
    }

    private double calculateTimeFactor(LocalDateTime deadline) {
        if (deadline == null) return 0.0;

        // Переводим в даты для сравнения дней
        LocalDate now = LocalDate.now();
        LocalDate deadlineDate = deadline.toLocalDate();

        // Разница в днях через EpochDay (количество дней с 1970 года)
        long daysRemaining = deadlineDate.toEpochDay() - now.toEpochDay();

        if (daysRemaining <= 0) return 10.0; // Дедлайн сегодня или прошел

        // Чем меньше дней, тем выше балл T (от 1 до 10) [cite: 170, 188]
        return 10.0 / Math.max(daysRemaining, 1);
    }
}