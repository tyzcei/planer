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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER) // Подгружаем сразу, чтобы видеть имя и фото
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false)
    private String lessonType;

    public CourseTeacher() {}

    public CourseTeacher(String groupNumber, Subject subject, Teacher teacher, String lessonType) {
        this.groupNumber = groupNumber;
        this.subject = subject;
        this.teacher = teacher;
        this.lessonType = lessonType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupNumber() { return groupNumber; }
    public void setGroupNumber(String groupNumber) { this.groupNumber = groupNumber; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public String getLessonType() { return lessonType; }
    public void setLessonType(String lessonType) { this.lessonType = lessonType; }
}