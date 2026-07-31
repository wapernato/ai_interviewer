package org.example.dto.response;

public record InternalAiProfileConfigResponse(
        Long id,
        String mode,
        String instruction,
        String modelName,
        String language,
        String answerStyle,
        String difficulty,
        String feedbackMode,
        boolean hintsEnabled,
        double temperature,
        int maxTokens
) {
}
