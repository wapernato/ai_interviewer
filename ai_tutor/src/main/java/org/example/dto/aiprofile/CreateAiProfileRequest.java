package org.example.dto.aiprofile;

import jakarta.validation.constraints.*;

public class CreateAiProfileRequest {

    @NotBlank(message = "Режим AI-профиля не может быть пустым.")
    @Size(min = 2, max = 100, message = "Режим AI-профиля должен быть от 2 до 100 символов.")
    private String mode;

    @Size(max = 1000, message = "Описание режима не должно превышать 1000 символов.")
    private String descriptionMode;

    @NotBlank(message = "Инструкция для AI-профиля не может быть пустой.")
    @Size(min = 10, max = 4000, message = "Инструкция должна быть от 10 до 4000 символов.")
    private String instructionMode;

    @NotBlank(message = "Название модели не может быть пустым.")
    @Size(max = 100, message = "Название модели не должно превышать 100 символов.")
    private String modelName;

    @NotBlank(message = "Язык не может быть пустым.")
    @Pattern(regexp = "ru|en", message = "Язык должен быть 'ru' или 'en'.")
    private String language;

    @Size(max = 100, message = "Стиль ответа не должен превышать 100 символов.")
    private String answerStyle;

    @NotBlank(message = "Сложность не может быть пустой.")
    @Pattern(
            regexp = "easy|medium|hard",
            message = "Сложность должна быть: easy, medium или hard."
    )
    private String difficulty;

    @NotBlank(message = "Режим обратной связи не может быть пустым.")
    @Pattern(
            regexp = "short|detailed|strict",
            message = "Режим обратной связи должен быть: short, detailed или strict."
    )
    private String feedbackMode;

    @NotNull(message = "Режим подсказок должен быть указан.")
    private Boolean hintMode;

    @NotNull(message = "Статус активности должен быть указан.")
    private Boolean active;

    @NotNull(message = "Temperature не должен быть пустым.")
    @DecimalMin(value = "0.0", message = "Temperature не может быть меньше 0.0.")
    @DecimalMax(value = "2.0", message = "Temperature не может быть больше 2.0.")
    private Double temperature;

    @NotNull(message = "Max tokens не должен быть пустым.")
    @Min(value = 1, message = "Max tokens должен быть больше 0.")
    @Max(value = 8000, message = "Max tokens не должен превышать 8000.")
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