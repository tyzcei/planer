package by.bsuir.semesterpassport.application.dto;

import java.time.LocalDateTime;

public class LabWorkDisplayDTO {
    private Long labId;
    private String title;
    private String subjectTitle;
    private Long subjectId; // Добавили для редактирования
    private String practitionerName;
    private int complexity;
    private LocalDateTime deadline;
    private String status; // Должно называться так для фронтенда
    private double priorityScore;

    public LabWorkDisplayDTO(Long labId, String title, String subjectTitle, Long subjectId,
                             String practitionerName, int complexity, LocalDateTime deadline,
                             String status, double priorityScore) {
        this.labId = labId;
        this.title = title;
        this.subjectTitle = subjectTitle;
        this.subjectId = subjectId;
        this.practitionerName = practitionerName;
        this.complexity = complexity;
        this.deadline = deadline;
        this.status = status;
        this.priorityScore = priorityScore;
    }

    // Геттеры для всех полей (обязательно!)
    public Long getLabId() { return labId; }
    public String getTitle() { return title; }
    public String getSubjectTitle() { return subjectTitle; }
    public Long getSubjectId() { return subjectId; }
    public String getPractitionerName() { return practitionerName; }
    public int getComplexity() { return complexity; }
    public LocalDateTime getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public double getPriorityScore() { return priorityScore; }
}