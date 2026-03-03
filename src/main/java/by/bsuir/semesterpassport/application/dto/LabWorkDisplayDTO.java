package by.bsuir.semesterpassport.application.dto;

import java.time.LocalDateTime;

public record LabWorkDisplayDTO(
        Long labId,
        String title,
        String subjectTitle,
        String practitionerName,
        Integer complexity,
        LocalDateTime deadline,
        String status,
        Double priorityScore
) {}