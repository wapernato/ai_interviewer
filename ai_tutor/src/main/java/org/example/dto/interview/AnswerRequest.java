package org.example.dto.interview;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AnswerRequest {
    @NotNull(message = "ID пользователя не должен быть пустым.")
    @Min(value = 1, message = "ID пользователя должен быть положительным числом")
    private Long userId;

    @NotNull(message = "ID вопроса не должен быть пустым.")
    @Min(value = 1, message = "ID вопроса должен быть положительным числом")
    private Long questionId;

    @NotBlank(message = "Текст ответа не может быть пустым.")
    @Size(max = 3000, message = "Текст ответа не должен превышать 3000 символов.")
    private String textAnswer;

    public AnswerRequest() {}

    public AnswerRequest(Long userId, Long questionId, String textAnswer){
        this.userId = userId;
        this.questionId = questionId;
        this.textAnswer = textAnswer;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
}
