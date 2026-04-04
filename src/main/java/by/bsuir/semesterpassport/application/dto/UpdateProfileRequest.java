package by.bsuir.semesterpassport.application.dto;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String newPassword
) {}