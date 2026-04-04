package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Это как раз для AUTO_INCREMENT
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, unique = true)
    private String groupNumber;

    private LocalDateTime updatedAt;

    // Пустой конструктор для JPA
    public Announcement() {}

    // Конструктор со всеми полями
    public Announcement(String content, String groupNumber, LocalDateTime updatedAt) {
        this.content = content;
        this.groupNumber = groupNumber;
        this.updatedAt = updatedAt;
    }

    // Геттеры и Сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getGroupNumber() { return groupNumber; }
    public void setGroupNumber(String groupNumber) { this.groupNumber = groupNumber; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}