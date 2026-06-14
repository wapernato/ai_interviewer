package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_profile_id")
    private AiProfile aiProfile;
    @Column(name = "answer_text", nullable = false)
    private String answerText;
    @Column(name = "model_name")
    private String modelName;

    public Answer() {}

    public Long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public AiProfile getAiProfile() {
        return aiProfile;
    }

    public String getAnswerText() {
        return answerText;
    }

    public String getModelName() {
        return modelName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setAiProfile(AiProfile aiProfile) {
        this.aiProfile = aiProfile;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answerText='" + answerText + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}