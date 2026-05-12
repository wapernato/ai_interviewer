package org.example.dto.aiprofile;

public class CreateAiProfileRequest {

    private String mode;
    private String descriptionMode;
    private String instructionMode;

    private String modelName;
    private String language;
    private String answerStyle;

    private String difficulty;
    private String feedbackMode;

    private Boolean hintMode;
    private Boolean active;

    private Double temperature;
    private Integer maxTokens;

    public CreateAiProfileRequest() {
    }

    public CreateAiProfileRequest(String mode,
                                  String descriptionMode,
                                  String instructionMode,
                                  String modelName,
                                  String language,
                                  String answerStyle,
                                  String difficulty,
                                  String feedbackMode,
                                  Boolean hintMode,
                                  Boolean active,
                                  Double temperature,
                                  Integer maxTokens) {
        this.mode = mode;
        this.descriptionMode = descriptionMode;
        this.instructionMode = instructionMode;
        this.modelName = modelName;
        this.language = language;
        this.answerStyle = answerStyle;
        this.difficulty = difficulty;
        this.feedbackMode = feedbackMode;
        this.hintMode = hintMode;
        this.active = active;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDescriptionMode() {
        return descriptionMode;
    }

    public void setDescriptionMode(String descriptionMode) {
        this.descriptionMode = descriptionMode;
    }

    public String getInstructionMode() {
        return instructionMode;
    }

    public void setInstructionMode(String instructionMode) {
        this.instructionMode = instructionMode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAnswerStyle() {
        return answerStyle;
    }

    public void setAnswerStyle(String answerStyle) {
        this.answerStyle = answerStyle;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getFeedbackMode() {
        return feedbackMode;
    }

    public void setFeedbackMode(String feedbackMode) {
        this.feedbackMode = feedbackMode;
    }

    public Boolean getHintMode() {
        return hintMode;
    }

    public void setHintMode(Boolean hintMode) {
        this.hintMode = hintMode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}