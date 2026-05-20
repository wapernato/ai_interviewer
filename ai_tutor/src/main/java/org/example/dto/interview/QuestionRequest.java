package org.example.dto.interview;

public class QuestionRequest {
    private Long userId;
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
