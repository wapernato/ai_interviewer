package org.example.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_profile_id")
    private AiProfile aiProfile;

    @Column(name = "text_question", nullable = false, columnDefinition = "text")
    private String textQuestion;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private QuestionDifficulty difficulty = QuestionDifficulty.MEDIUM;

    @Column(name = "source", length = 30)
    private String source;

    @Column(name = "language", length = 20)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_status", nullable = false, length = 30)
    private QuestionPublicationStatus publicationStatus = QuestionPublicationStatus.PENDING_REVIEW;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Question() {};

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTextQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User user) {
        this.createdByUser = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public AiProfile getAiProfile() {
        return aiProfile;
    }

    public void setAiProfile(AiProfile aiProfile) {
        this.aiProfile = aiProfile;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getLanguage() {
        return language;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public String getSource() {
        return source;
    }

    public QuestionPublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(QuestionPublicationStatus publicationStatus) {
        this.publicationStatus = publicationStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", textQuestion='" + textQuestion + '\'' +
                ", difficulty=" + difficulty +
                ", source='" + source + '\'' +
                ", language='" + language + '\'' +
                ", publicationStatus=" + publicationStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}
