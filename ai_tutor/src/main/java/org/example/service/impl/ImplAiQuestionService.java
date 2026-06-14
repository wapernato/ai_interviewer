package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.model.AiProfile;
import org.example.model.Topic;
import org.example.service.AiQuestionGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImplAiQuestionService implements AiQuestionGenerator {

    @Transactional
    @Override
    public String generatedQuestion(Topic topic, AiProfile aiProfile) {
        if (topic == null) {
            throw new BadRequestException("Topic обязателен для генерации вопроса.");
        }

        if (aiProfile == null) {
            throw new BadRequestException("AiProfile обязателен для генерации вопроса.");
        }

        String topicName = topic.getName();
        String difficulty = aiProfile.getDifficulty();
        String mode = aiProfile.getMode();

        if (topicName == null || topicName.isBlank()) {
            throw new BadRequestException("Название темы не может быть пустым.");
        }

        if (difficulty == null || difficulty.isBlank()) {
            difficulty = "junior";
        }

        if (mode == null || mode.isBlank()) {
            mode = "default";
        }

        return buildQuestion(topicName, difficulty, mode);
    }

    private String buildQuestion(String topicName, String difficulty, String mode) {
        String normalizedTopic = topicName.trim().toLowerCase();

        if (normalizedTopic.contains("java")) {
            return "Вопрос по теме \"" + topicName + "\". "
                    + "Объясните разницу между ArrayList и LinkedList. "
                    + "В каких случаях лучше использовать ArrayList, а в каких LinkedList? "
                    + "Уровень сложности: " + difficulty + ". "
                    + "Режим интервьюера: " + mode + ".";
        }

        if (normalizedTopic.contains("sql") || normalizedTopic.contains("postgres")) {
            return "Вопрос по теме \"" + topicName + "\". "
                    + "Объясните разницу между INNER JOIN и LEFT JOIN. "
                    + "Приведите пример, когда LEFT JOIN нужен в backend-приложении. "
                    + "Уровень сложности: " + difficulty + ". "
                    + "Режим интервьюера: " + mode + ".";
        }

        if (normalizedTopic.contains("spring")) {
            return "Вопрос по теме \"" + topicName + "\". "
                    + "Объясните, что делает аннотация @RestController в Spring Boot. "
                    + "Чем она отличается от обычного @Controller? "
                    + "Уровень сложности: " + difficulty + ". "
                    + "Режим интервьюера: " + mode + ".";
        }

        if (normalizedTopic.contains("algorithm") || normalizedTopic.contains("алгоритм")) {
            return "Вопрос по теме \"" + topicName + "\". "
                    + "Объясните, как работает HashMap и почему операции поиска обычно выполняются за O(1). "
                    + "Какие ситуации могут ухудшить производительность? "
                    + "Уровень сложности: " + difficulty + ". "
                    + "Режим интервьюера: " + mode + ".";
        }

        return "Вопрос по теме \"" + topicName + "\". "
                + "Расскажите основные идеи этой темы, приведите пример из backend-разработки "
                + "и объясните, где это может применяться в реальном проекте. "
                + "Уровень сложности: " + difficulty + ". "
                + "Режим интервьюера: " + mode + ".";
    }
}