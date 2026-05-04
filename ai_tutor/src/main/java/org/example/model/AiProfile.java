package org.example.model;

public class AiProfile {

    private Long id;

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

    public AiProfile() {
    }

    public AiProfile(String mode,
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

    public AiProfile(Long id,
                     String mode,
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
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public String getMode() {
        return mode;
    }

    public String getDescriptionMode() {
        return descriptionMode;
    }

    public String getInstructionMode() {
        return instructionMode;
    }

    public String getModelName() {
        return modelName;
    }

    public String getLanguage() {
        return language;
    }

    public String getAnswerStyle() {
        return answerStyle;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getFeedbackMode() {
        return feedbackMode;
    }

    public Boolean getHintMode() {
        return hintMode;
    }

    public Boolean getActive() {
        return active;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setDescriptionMode(String descriptionMode) {
        this.descriptionMode = descriptionMode;
    }

    public void setInstructionMode(String instructionMode) {
        this.instructionMode = instructionMode;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAnswerStyle(String answerStyle) {
        this.answerStyle = answerStyle;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setFeedbackMode(String feedbackMode) {
        this.feedbackMode = feedbackMode;
    }

    public void setHintMode(Boolean hintMode) {
        this.hintMode = hintMode;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public String toString() {
        return "AiProfile{" +
                "id=" + id +
                ", mode='" + mode + '\'' +
                ", descriptionMode='" + descriptionMode + '\'' +
                ", instructionMode='" + instructionMode + '\'' +
                ", modelName='" + modelName + '\'' +
                ", language='" + language + '\'' +
                ", answerStyle='" + answerStyle + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", feedbackMode='" + feedbackMode + '\'' +
                ", hintMode=" + hintMode +
                ", active=" + active +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                '}';
    }
}