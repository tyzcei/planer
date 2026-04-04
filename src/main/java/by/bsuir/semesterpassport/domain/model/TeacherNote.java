package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_notes")
public class TeacherNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupNumber;

    @Column(nullable = false)
    private String bsuirUrlId;

    @Column(columnDefinition = "TEXT")
    private String noteText;

    private LocalDateTime updatedAt;

    public TeacherNote() {}

    public TeacherNote(String groupNumber, String bsuirUrlId, String noteText) {
        this.groupNumber = groupNumber;
        this.bsuirUrlId = bsuirUrlId;
        this.noteText = noteText;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public String getGroupNumber() { return groupNumber; }
    public String getBsuirUrlId() { return bsuirUrlId; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}