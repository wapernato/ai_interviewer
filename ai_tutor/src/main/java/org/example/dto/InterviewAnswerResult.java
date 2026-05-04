package org.example.dto;

public class InterviewAnswerResult {

    private Long userId;
    private Long questionId;
    private Long answerId;

    private String questionText;
    private String userAnswerText;

    public InterviewAnswerResult() {
    }

    public InterviewAnswerResult(Long userId,
                                 Long questionId,
                                 Long answerId,
                                 String questionText,
                                 String userAnswerText) {
        this.userId = userId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.questionText = questionText;
        this.userAnswerText = userAnswerText;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getUserAnswerText() {
        return userAnswerText;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setUserAnswerText(String userAnswerText) {
        this.userAnswerText = userAnswerText;
    }

    @Override
    public String toString() {
        return "InterviewAnswerResult{" +
                "userId=" + userId +
                ", questionId=" + questionId +
                ", answerId=" + answerId +
                ", questionText='" + questionText + '\'' +
                ", userAnswerText='" + userAnswerText + '\'' +
                '}';
    }

}
