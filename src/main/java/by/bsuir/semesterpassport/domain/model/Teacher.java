package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bsuir_url_id", nullable = false, unique = true)
    private String bsuirUrlId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "photo_link", length = 500)
    private String photoLink;

    public Teacher() {}

    public Teacher(String bsuirUrlId, String fullName, String photoLink) {
        this.bsuirUrlId = bsuirUrlId;
        this.fullName = fullName;
        this.photoLink = photoLink;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBsuirUrlId() { return bsuirUrlId; }
    public void setBsuirUrlId(String bsuirUrlId) { this.bsuirUrlId = bsuirUrlId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhotoLink() { return photoLink; }
    public void setPhotoLink(String photoLink) { this.photoLink = photoLink; }
}