package by.bsuir.semesterpassport.application.dto;

public record TeacherWithNoteDto(
        String subjectTitle,
        String teacherName,
        String bsuirUrlId,
        String lessonType,
        String photoLink,
        String noteText // Может быть null, если староста еще ничего не написал
) {}