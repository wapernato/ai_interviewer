package org.example.model;

public class Question {

    private Long id;
    private Long userId;
    private Long topicId;
    private String textQuestion;
    private String source;
    private String language;

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

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public Long getUserId() {
        return userId;
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

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", userId=" + userId +
                ", topicId=" + topicId +
                ", textQuestion='" + textQuestion + '\'' +
                ", source='" + source + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
