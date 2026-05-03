package org.example.model;

public class AiProfile {

    private Long id;
    private String mode;
    private String descriptionMode;
    private String instructionMode;
    private String language;
    private String answerStyle;
    private Boolean hintMode;
    private Boolean active;

    public AiProfile() {
    }

    public AiProfile(String mode,
                     String descriptionMode,
                     String instructionMode,
                     String language,
                     String answerStyle,
                     Boolean hintMode,
                     Boolean active) {
        this.mode = mode;
        this.descriptionMode = descriptionMode;
        this.instructionMode = instructionMode;
        this.language = language;
        this.answerStyle = answerStyle;
        this.hintMode = hintMode;
        this.active = active;
    }

    public AiProfile(Long id,
                     String mode,
                     String descriptionMode,
                     String instructionMode,
                     String language,
                     String answerStyle,
                     Boolean hintMode,
                     Boolean active) {
        this.id = id;
        this.mode = mode;
        this.descriptionMode = descriptionMode;
        this.instructionMode = instructionMode;
        this.language = language;
        this.answerStyle = answerStyle;
        this.hintMode = hintMode;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "AiProfile{" +
                "id=" + id +
                ", mode='" + mode + '\'' +
                ", descriptionMode='" + descriptionMode + '\'' +
                ", instructionMode='" + instructionMode + '\'' +
                ", language='" + language + '\'' +
                ", answerStyle='" + answerStyle + '\'' +
                ", hintMode=" + hintMode +
                ", active=" + active +
                '}';
    }
}