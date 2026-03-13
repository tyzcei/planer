package by.bsuir.semesterpassport.application.dto;

import java.util.List;

public record GroupSubjectRequest(
        String groupNumber,
        List<Long> subjectIds
) {}