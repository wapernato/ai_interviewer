package org.example.model;

public class Answer {

    private Long id;
    private Long questionId;
    private Long aiProfileId;
    private String answerText;
    private String modelName;

    public Answer() {}

    public Answer(Long id, Long questionId, Long aiProfileId, String answerText, String modelName) {
        this.id = id;
        this.questionId = questionId;
        this.aiProfileId = aiProfileId;
        this.answerText = answerText;
        this.modelName = modelName;
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAiProfileId() {
        return aiProfileId;
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

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setAiProfileId(Long aiProfileId) {
        this.aiProfileId = aiProfileId;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}