package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.model.AiProfile;
import org.example.model.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiQuestionServiceImplTest {

    private AiQuestionServiceImpl aiQuestionService;

    @BeforeEach
    void setUp() {
        aiQuestionService = new AiQuestionServiceImpl();
    }

    @Test
    void generatedQuestion_shouldThrowBadRequest_whenTopicIsNull() {
        assertThatThrownBy(() -> aiQuestionService.generatedQuestion(null, createProfile("middle", "strict")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Topic обязателен для генерации вопроса.");
    }

    @Test
    void generatedQuestion_shouldThrowBadRequest_whenAiProfileIsNull() {
        assertThatThrownBy(() -> aiQuestionService.generatedQuestion(new Topic("Java"), null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("AiProfile обязателен для генерации вопроса.");
    }

    @Test
    void generatedQuestion_shouldThrowBadRequest_whenTopicNameIsBlank() {
        assertThatThrownBy(() -> aiQuestionService.generatedQuestion(
                new Topic("   "), createProfile("middle", "strict")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не может быть пустым.");
    }

    @Test
    void generatedQuestion_shouldReturnJavaQuestion_whenTopicContainsJava() {
        String result = aiQuestionService.generatedQuestion(
                new Topic("Java Core"), createProfile("middle", "strict"));

        assertThat(result)
                .contains("ArrayList и LinkedList")
                .contains("Уровень сложности: middle")
                .contains("Режим интервьюера: strict");
    }

    @Test
    void generatedQuestion_shouldReturnSqlQuestion_whenTopicContainsPostgres() {
        String result = aiQuestionService.generatedQuestion(
                new Topic("PostgreSQL"), createProfile("middle", "strict"));

        assertThat(result).contains("INNER JOIN и LEFT JOIN");
    }

    @Test
    void generatedQuestion_shouldReturnSpringQuestion_whenTopicContainsSpring() {
        String result = aiQuestionService.generatedQuestion(
                new Topic("Spring Boot"), createProfile("middle", "strict"));

        assertThat(result).contains("@RestController");
    }

    @Test
    void generatedQuestion_shouldReturnAlgorithmQuestion_whenTopicContainsAlgorithm() {
        String result = aiQuestionService.generatedQuestion(
                new Topic("Алгоритмы"), createProfile("middle", "strict"));

        assertThat(result).contains("HashMap").contains("O(1)");
    }

    @Test
    void generatedQuestion_shouldReturnGenericQuestion_whenTopicIsUnknown() {
        String result = aiQuestionService.generatedQuestion(
                new Topic("Docker"), createProfile("middle", "strict"));

        assertThat(result).contains("основные идеи этой темы").contains("Docker");
    }

    @Test
    void generatedQuestion_shouldUseDefaults_whenDifficultyAndModeAreBlank() {
        String result = aiQuestionService.generatedQuestion(
                new Topic("Java"), createProfile("   ", null));

        assertThat(result)
                .contains("Уровень сложности: junior")
                .contains("Режим интервьюера: default");
    }

    private AiProfile createProfile(String difficulty, String mode) {
        AiProfile profile = new AiProfile();
        profile.setDifficulty(difficulty);
        profile.setMode(mode);
        return profile;
    }
}
