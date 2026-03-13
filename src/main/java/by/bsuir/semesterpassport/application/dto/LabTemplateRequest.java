package by.bsuir.semesterpassport.application.dto;

import java.time.LocalDateTime;

// Для UC-6: Создание шаблона лаб для группы
public record LabTemplateRequest(
        Long subjectId,
        Long groupId,
        Integer labCount,
        LocalDateTime globalDeadline,
        Integer defaultComplexity
) {}