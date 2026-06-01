package org.example.dto.interview;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InterviewAnswerResult {
    @NotNull(message = "ID пользователя не должен быть пустым.")
    @Min(value = 1, message = "ID пользователя должен быть положительным числом")
    private Long userId;

    @NotNull(message = "ID вопроса не должен быть пустым.")
    @Min(value = 1, message = "ID вопроса должен быть положительным числом")
    private Long questionId;

    @NotNull(message = "ID ответа не должен быть пустым.")
    @Min(value = 1, message = "ID ответа должен быть положительным числом")
    private Long answerId;

    @NotBlank(message = "Вопрос не может быть пустым.")
    private String questionText;

    @NotBlank(message = "Ответ пользователя не может быть пустым.")
    private String userAnswerText;

    @NotBlank(message = "Обратная связь не может быть пустой.")
    private String feedback;

    public InterviewAnswerResult() {
    }

    public InterviewAnswerResult(Long userId,
                                 Long questionId,
                                 Long answerId,
                                 String questionText,
                                 String userAnswerText,
                                 String feedback) {
        this.userId = userId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.questionText = questionText;
        this.userAnswerText = userAnswerText;
        this.feedback = feedback;
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

    public String getFeedback() { return feedback; }

    public void setFeedback(String feedback) { this.feedback = feedback; }

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
                ", feedback='" + feedback + '\'' +
                '}';
    }

}
