package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_works")
public class LabWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    private Teacher practitioner; // Тот самый практик, который принимает лабы

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer complexity; // Параметр S (1-5) [cite: 170]

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private LabStatus currentStatus; // RECEIVED, CODED, READY, SUBMITTED, PROTECTED [cite: 374]

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LabWork() {}

    // Жизненный цикл сущности (Метаданные)
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentStatus == null) {
            currentStatus = LabStatus.RECEIVED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Геттеры и Сеттеры (Полный список)
    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(Teacher practitioner) {
        this.practitioner = practitioner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LabStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(LabStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}