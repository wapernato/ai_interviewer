package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.model.AiProfile;
import org.example.model.Question;
import org.example.service.AiAnswerEvaluator;
import org.springframework.stereotype.Service;

@Service
public class AiAnswerEvaluatorImpl implements AiAnswerEvaluator {

    @Override
    public String evaluateAnswer(Question question, AiProfile aiProfile, String userAnswerText) {
        if (question == null) {
            throw new BadRequestException("Question обязателен для оценки ответа.");
        }

        if (aiProfile == null) {
            throw new BadRequestException("AiProfile обязателен для оценки ответа.");
        }

        if (userAnswerText == null || userAnswerText.isBlank()) {
            throw new BadRequestException("Ответ пользователя не может быть пустым.");
        }

        String trimmedAnswer = userAnswerText.trim();
        String mode = aiProfile.getMode() == null ? "default" : aiProfile.getMode();
        String difficulty = aiProfile.getDifficulty() == null ? "junior" : aiProfile.getDifficulty();
        String feedbackMode = aiProfile.getFeedbackMode() == null ? "standard" : aiProfile.getFeedbackMode();

        int answerLength = trimmedAnswer.length();

        StringBuilder feedback = new StringBuilder();

        feedback.append("AI-feedback по ответу. ");
        feedback.append("Режим интервьюера: ").append(mode).append(". ");
        feedback.append("Сложность: ").append(difficulty).append(". ");
        feedback.append("Тип проверки: ").append(feedbackMode).append(". ");

        if (answerLength < 80) {
            feedback.append("Ответ слишком короткий. ");
            feedback.append("На собеседовании такого ответа, скорее всего, будет недостаточно. ");
            feedback.append("Нужно добавить определение, пример и объяснение, где это применяется. ");
        } else if (answerLength < 250) {
            feedback.append("Ответ в целом принят, но раскрыт не полностью. ");
            feedback.append("Стоит добавить больше деталей, сравнение подходов и практический пример. ");
        } else {
            feedback.append("Ответ выглядит достаточно развернутым. ");
            feedback.append("Хорошо, что ты пытаешься объяснить тему подробнее. ");
        }

        feedback.append("Рекомендация: отвечай по структуре: ");
        feedback.append("1) краткое определение; ");
        feedback.append("2) ключевые отличия; ");
        feedback.append("3) пример из backend-разработки; ");
        feedback.append("4) вывод, когда это использовать.");

        return feedback.toString();
    }
}
