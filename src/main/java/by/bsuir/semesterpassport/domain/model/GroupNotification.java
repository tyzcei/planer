package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_notifications")
public class GroupNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupNumber;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime createdAt;

    public GroupNotification() {}

    public GroupNotification(String groupNumber, String message, boolean isRead, LocalDateTime createdAt) {
        this.groupNumber = groupNumber;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupNumber() { return groupNumber; }
    public void setGroupNumber(String groupNumber) { this.groupNumber = groupNumber; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}