package by.bsuir.semesterpassport.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_artifacts")
public class LabArtifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artifactId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id")
    private LabWork labWork;

    private String gitLink;
    private String cloudLink;
    private LocalDateTime createdAt;

    public LabArtifact() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getArtifactId() { return artifactId; }
    public void setArtifactId(Long artifactId) { this.artifactId = artifactId; }
    public String getGitLink() { return gitLink; }
    public void setGitLink(String gitLink) { this.gitLink = gitLink; }
    public String getCloudLink() { return cloudLink; }
    public void setCloudLink(String cloudLink) { this.cloudLink = cloudLink; }
}