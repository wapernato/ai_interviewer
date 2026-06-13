package org.example.dto.user;



public class UserHistoryItem {

    private Long questionId;
    private String username;
    private String topicName;
    private String textQuestion;
    private String answerText;
    private String modelName;

    public UserHistoryItem() {}

    public UserHistoryItem(Long questionId,
                           String username,
                           String topicName,
                           String textQuestion,
                           String answerText,
                           String modelName) {
        this.questionId = questionId;
        this.username = username;
        this.topicName = topicName;
        this.textQuestion = textQuestion;
        this.answerText = answerText;
        this.modelName = modelName;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public void setTextQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String toString() {
        return "UserHistoryItem{" +
                "questionId=" + questionId +
                ", username='" + username + '\'' +
                ", topicName='" + topicName + '\'' +
                ", textQuestion='" + textQuestion + '\'' +
                ", answerText='" + answerText + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
