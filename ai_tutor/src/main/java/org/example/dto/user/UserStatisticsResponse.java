package org.example.dto.user;

public record UserStatisticsResponse(
        long totalQuestions,
        long totalAnswers,
        long unansweredQuestions,
        double completionRate
) {}
