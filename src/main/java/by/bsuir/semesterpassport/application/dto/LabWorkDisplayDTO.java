package by.bsuir.semesterpassport.application.dto;

import java.time.LocalDateTime;

public record LabWorkDisplayDTO(
        Long labId,
        String title,
        String subjectTitle,
        String practitionerName, // Вернули преподавателя на 4-е место!
        Integer complexity,
        LocalDateTime deadline,
        String status,
        Double priorityScore
) {}