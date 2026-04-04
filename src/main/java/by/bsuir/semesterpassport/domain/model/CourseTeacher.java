package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_teachers")
public class CourseTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupNumber;

    @Column(nullable = false)
    private String subjectTitle;

    @Column(nullable = false)
    private String teacherName;

    @Column(nullable = false)
    private String bsuirUrlId;

    @Column(nullable = false)
    private String lessonType; // ЛК, ПЗ, ЛР

    @Column(length = 500)
    private String photoLink;

    public CourseTeacher() {}

    public CourseTeacher(String groupNumber, String subjectTitle, String teacherName, String bsuirUrlId, String lessonType, String photoLink) {
        this.groupNumber = groupNumber;
        this.subjectTitle = subjectTitle;
        this.teacherName = teacherName;
        this.bsuirUrlId = bsuirUrlId;
        this.lessonType = lessonType;
        this.photoLink = photoLink;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public String getGroupNumber() { return groupNumber; }
    public String getSubjectTitle() { return subjectTitle; }
    public String getTeacherName() { return teacherName; }
    public String getBsuirUrlId() { return bsuirUrlId; }
    public String getLessonType() { return lessonType; }
    public String getPhotoLink() { return photoLink; }
}