package org.example.dto.response;

import jakarta.persistence.*;
import org.example.model.Topic;
import org.example.model.User;

public class QuestionResponse {

    private Long id;
    private Long userId;
    private Long topicId;
    private String textQuestion;
    private String source;
    private String language;

    public QuestionResponse(){
    }

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

    public String getLanguage() {
        return language;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public String getSource() {
        return source;
    }
}
