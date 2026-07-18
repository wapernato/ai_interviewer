package org.example.integration.ai;

import org.example.dto.ai.QuestionGenerationResult;
import org.example.model.AiProfile;

public interface AiQuestionGenerator {
    QuestionGenerationResult generate(String requestedTopic, AiProfile aiProfile);
}
