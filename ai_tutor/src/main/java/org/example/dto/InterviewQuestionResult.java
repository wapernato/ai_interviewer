package org.example.dto;

public class InterviewQuestionResult {

    private Long questionId;
    private Long userId;
    private Long topicId;
    private Long aiProfileId;

    private String topicName;
    private String questionText;

    private String aiMode;
    private String difficulty;

    public InterviewQuestionResult() {
    }

    public InterviewQuestionResult(Long questionId,
                                   Long userId,
                                   Long topicId,
                                   Long aiProfileId,
                                   String topicName,
                                   String questionText,
                                   String aiMode,
                                   String difficulty) {
        this.questionId = questionId;
        this.userId = userId;
        this.topicId = topicId;
        this.aiProfileId = aiProfileId;
        this.topicName = topicName;
        this.questionText = questionText;
        this.aiMode = aiMode;
        this.difficulty = difficulty;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Long getAiProfileId() {
        return aiProfileId;
    }

    public void setAiProfileId(Long aiProfileId) {
        this.aiProfileId = aiProfileId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAiMode() {
        return aiMode;
    }

    public void setAiMode(String aiMode) {
        this.aiMode = aiMode;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "InterviewQuestionResult{" +
                "questionId=" + questionId +
                ", userId=" + userId +
                ", topicId=" + topicId +
                ", aiProfileId=" + aiProfileId +
                ", topicName='" + topicName + '\'' +
                ", questionText='" + questionText + '\'' +
                ", aiMode='" + aiMode + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}