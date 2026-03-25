package by.bsuir.semesterpassport.application.dto;

import java.time.LocalDateTime;

// Для индивидуального добавления лабы студентом
public record LabWorkRequest(
        String title,
        Integer complexity,
        Long userId,
        Long subjectId,
        LocalDateTime deadline
) {}