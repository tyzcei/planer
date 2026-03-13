package by.bsuir.semesterpassport.application.dto;

public record AnnouncementRequest(
        Long groupId,
        String title,
        String message
) {}