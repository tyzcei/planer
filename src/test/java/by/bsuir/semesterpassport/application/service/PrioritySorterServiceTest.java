package by.bsuir.semesterpassport.application.service;

import by.bsuir.semesterpassport.domain.model.LabWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrioritySorterServiceTest {

    private PrioritySorterService sorterService;

    @BeforeEach
    void setUp() {
        sorterService = new PrioritySorterService();
    }

    // Тест 1
    @Test
    @DisplayName("calculateScore: Проверка расчета с дедлайном в будущем (5 дней)")
    void calculateScore_FutureDeadline() {
        LabWork lab = new LabWork();
        lab.setComplexity(3); // S = 3
        lab.setDeadline(LocalDateTime.now().plusDays(5)); // T = 10.0 / 5 = 2.0

        double score = sorterService.calculateScore(lab);

        assertEquals(5.0, score, 0.1, "Ожидается Score = 5.0 (3 + 2.0)");
    }

    // Тест 2
    @Test
    @DisplayName("calculateScore: Дедлайн просрочен (максимальный временной фактор)")
    void calculateScore_PastDeadline() {
        LabWork lab = new LabWork();
        lab.setComplexity(2);
        lab.setDeadline(LocalDateTime.now().minusDays(2)); // T должно быть 10.0

        double score = sorterService.calculateScore(lab);

        assertEquals(12.0, score, 0.1, "Ожидается Score = 12.0 (2 + 10.0)");
    }

    // Тест 3
    @Test
    @DisplayName("calculateScore: Null-значения (защита от NPE)")
    void calculateScore_NullValues() {
        LabWork lab = new LabWork();
        // Complexity и Deadline = null

        double score = sorterService.calculateScore(lab);

        assertEquals(1.0, score, 0.1, "Если все null, дефолтная сложность 1, а T = 0.0");
    }

    // Тест 4
    @Test
    @DisplayName("sortLabsByPriority: Правильная сортировка списка (по убыванию)")
    void sortLabsByPriority_ValidSorting() {
        LabWork urgentLab = new LabWork();
        urgentLab.setComplexity(5);
        urgentLab.setDeadline(LocalDateTime.now().minusDays(1)); // Просрочена, очень высокий приоритет

        LabWork chillLab = new LabWork();
        chillLab.setComplexity(1);
        chillLab.setDeadline(LocalDateTime.now().plusDays(20)); // Низкий приоритет

        List<LabWork> unsorted = Arrays.asList(chillLab, urgentLab);

        List<LabWork> sorted = sorterService.sortLabsByPriority(unsorted);

        assertEquals(urgentLab, sorted.get(0), "Самая срочная лаба должна быть первой");
        assertEquals(chillLab, sorted.get(1), "Менее срочная лаба должна быть второй");
    }

    // Тест 5
    @Test
    @DisplayName("sortLabsByPriority: Обработка null-списка")
    void sortLabsByPriority_NullList() {
        List<LabWork> result = sorterService.sortLabsByPriority(null);
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Должен вернуться пустой список, а не NullPointerException");
    }
}