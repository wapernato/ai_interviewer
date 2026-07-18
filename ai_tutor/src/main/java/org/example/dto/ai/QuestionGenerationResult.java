package org.example.dto.ai;

import org.example.model.QuestionDifficulty;

public class QuestionGenerationResult {
    private String questionText;
    private String normalizedTopic;
    private QuestionDifficulty difficulty;

    public QuestionGenerationResult() {}

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getNormalizedTopic() {
        return normalizedTopic;
    }

    public void setNormalizedTopic(String normalizedTopic) {
        this.normalizedTopic = normalizedTopic;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
