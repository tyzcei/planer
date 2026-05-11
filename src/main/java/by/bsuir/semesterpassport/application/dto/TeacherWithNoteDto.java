package by.bsuir.semesterpassport.application.dto;

import java.io.Serializable; // Обязательно нужен этот импорт!

public record TeacherWithNoteDto (
        String subjectTitle,
        String teacherName,
        String bsuirUrlId,
        String lessonType,
        String photoLink,
        String noteText // Может быть null, если староста еще ничего не написал
) implements Serializable {} // <--- Добавили разрешение на кэширование