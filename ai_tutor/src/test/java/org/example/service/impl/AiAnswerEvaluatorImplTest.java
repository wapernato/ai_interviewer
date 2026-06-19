package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.model.AiProfile;
import org.example.model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiAnswerEvaluatorImplTest {

    private AiAnswerEvaluatorImpl evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new AiAnswerEvaluatorImpl();
    }

    @Test
    void evaluateAnswer_shouldThrowBadRequest_whenQuestionIsNull() {
        assertThatThrownBy(() -> evaluator.evaluateAnswer(null, createProfile(), "Answer"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Question обязателен для оценки ответа.");
    }

    @Test
    void evaluateAnswer_shouldThrowBadRequest_whenAiProfileIsNull() {
        assertThatThrownBy(() -> evaluator.evaluateAnswer(new Question(), null, "Answer"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("AiProfile обязателен для оценки ответа.");
    }

    @Test
    void evaluateAnswer_shouldThrowBadRequest_whenAnswerIsBlank() {
        assertThatThrownBy(() -> evaluator.evaluateAnswer(new Question(), createProfile(), "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Ответ пользователя не может быть пустым.");
    }

    @Test
    void evaluateAnswer_shouldReturnShortAnswerFeedback_whenAnswerHasLessThan80Characters() {
        String result = evaluator.evaluateAnswer(new Question(), createProfile(), "Short answer");

        assertThat(result).contains("Ответ слишком короткий");
    }

    @Test
    void evaluateAnswer_shouldReturnPartialFeedback_whenAnswerHasLessThan250Characters() {
        String result = evaluator.evaluateAnswer(new Question(), createProfile(), "a".repeat(100));

        assertThat(result).contains("Ответ в целом принят, но раскрыт не полностью");
    }

    @Test
    void evaluateAnswer_shouldReturnDetailedFeedback_whenAnswerHasAtLeast250Characters() {
        String result = evaluator.evaluateAnswer(new Question(), createProfile(), "a".repeat(250));

        assertThat(result).contains("Ответ выглядит достаточно развернутым");
    }

    @Test
    void evaluateAnswer_shouldIncludeProfileSettings() {
        AiProfile profile = createProfile();
        profile.setMode("strict");
        profile.setDifficulty("middle");
        profile.setFeedbackMode("detailed");

        String result = evaluator.evaluateAnswer(new Question(), profile, "a".repeat(100));

        assertThat(result)
                .contains("Режим интервьюера: strict")
                .contains("Сложность: middle")
                .contains("Тип проверки: detailed");
    }

    @Test
    void evaluateAnswer_shouldUseDefaults_whenProfileSettingsAreNull() {
        String result = evaluator.evaluateAnswer(new Question(), new AiProfile(), "a".repeat(100));

        assertThat(result)
                .contains("Режим интервьюера: default")
                .contains("Сложность: junior")
                .contains("Тип проверки: standard");
    }

    private AiProfile createProfile() {
        AiProfile profile = new AiProfile();
        profile.setMode("default");
        profile.setDifficulty("junior");
        profile.setFeedbackMode("standard");
        return profile;
    }
}
