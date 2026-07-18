package org.example.integration.ai;

import org.example.dto.ai.QuestionGenerationResult;
import org.example.exception.BadRequestException;
import org.example.model.AiProfile;
import org.example.model.QuestionDifficulty;
import org.springframework.stereotype.Component;

@Component
public class StubAiQuestionGenerator implements AiQuestionGenerator {

    @Override
    public QuestionGenerationResult generate(String requestedTopic, AiProfile aiProfile) {
        if (requestedTopic == null) {
            throw new BadRequestException("Текст темы не должен быть null.");
        }

        String normalizedTopic = requestedTopic.trim();

        if (normalizedTopic.isBlank()) {
            throw new BadRequestException("Текст темы не должен быть пустым.");
        }

        QuestionGenerationResult result = new QuestionGenerationResult();
        result.setQuestionText("Временный вопрос по теме: " + normalizedTopic);
        result.setNormalizedTopic(normalizedTopic);
        result.setDifficulty(QuestionDifficulty.HARD);

        return result;
    }
}
