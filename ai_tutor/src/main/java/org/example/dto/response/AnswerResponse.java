package org.example.dto.response;

public class AnswerResponse {


    private Long id;
    private Long questionId;
    private Long aiProfileId;
    private String answerText;
    private String modelName;

    public AnswerResponse(){
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
