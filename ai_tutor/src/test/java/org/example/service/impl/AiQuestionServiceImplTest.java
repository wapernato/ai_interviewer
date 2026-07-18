package org.example.service.impl;

import org.example.dto.ai.QuestionGenerationResult;
import org.example.exception.BadRequestException;
import org.example.integration.ai.StubAiQuestionGenerator;
import org.example.model.AiProfile;
import org.example.model.QuestionDifficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiQuestionServiceImplTest {

    private StubAiQuestionGenerator aiQuestionGenerator;

    @BeforeEach
    void setUp() {
        aiQuestionGenerator = new StubAiQuestionGenerator();
    }


    private AiProfile createProfile(String difficulty, String mode) {
        AiProfile profile = new AiProfile();
        profile.setDifficulty(difficulty);
        profile.setMode(mode);
        return profile;
    }

    @Test
    void generate_shouldThrowBadRequest_whenTopicIsNull() {
        assertThatThrownBy(() -> aiQuestionGenerator.generate(null, createProfile("middle", "strict")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст темы не должен быть null.");
    }

    @Test
    void generate_shouldThrowBadRequest_whenTopicIsBlank() {
        assertThatThrownBy(() -> aiQuestionGenerator.generate("   ", createProfile("middle", "strict")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст темы не должен быть пустым.");
    }

    @Test
    void generate_shouldReturnQuestionResult_whenTopicIsValid() {
        QuestionGenerationResult result = aiQuestionGenerator.generate(
                "  Java Core  ",
                createProfile("middle", "strict")
        );

        assertThat(result.getQuestionText()).isEqualTo("Временный вопрос по теме: Java Core");
        assertThat(result.getNormalizedTopic()).isEqualTo("Java Core");
        assertThat(result.getDifficulty()).isEqualTo(QuestionDifficulty.HARD);
    }
}
