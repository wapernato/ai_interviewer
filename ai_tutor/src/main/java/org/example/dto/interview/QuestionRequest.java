package org.example.dto.interview;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class QuestionRequest {
    @NotNull(message = "ID пользователя не должен быть пустым.")
    @Min(value = 1, message = "ID пользователя должно быть положительным числом")
    private Long userId;

    @NotNull(message = "ID темы не должен быть пустым.")
    @Min(value = 1, message = "ID темы должно быть положительным числом")
    private Long topicId;

    public QuestionRequest() {}

    public QuestionRequest(Long userId, Long topicId ){
        this.userId = userId;
        this.topicId = topicId;
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

    public Long getUserId() {
        return userId;
    }
}
