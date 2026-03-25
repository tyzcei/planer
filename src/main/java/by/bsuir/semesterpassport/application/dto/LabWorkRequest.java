package by.bsuir.semesterpassport.application.dto;

import java.time.LocalDateTime;

public class LabWorkRequest {
    private String title;
    private Integer complexity;
    private LocalDateTime deadline;
    private Long subjectId;
    private Long userId;

    public LabWorkRequest() {}

    // Геттеры
    public String getTitle() { return title; }
    public Integer getComplexity() { return complexity; }
    public LocalDateTime getDeadline() { return deadline; }
    public Long getSubjectId() { return subjectId; }
    public Long getUserId() { return userId; }

    // Сеттеры
    public void setTitle(String title) { this.title = title; }
    public void setComplexity(Integer complexity) { this.complexity = complexity; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public void setUserId(Long userId) { this.userId = userId; }
}