package org.example.service.impl;

import org.example.model.AiProfile;
import org.example.model.Topic;
import org.example.service.AiQuestionGenerator;
import org.springframework.stereotype.Service;

@Service
public class ImplAiQuestionService implements AiQuestionGenerator {

    @Override
    public String generatedQuestion(Topic topic, AiProfile aiProfile){

        if (topic == null || aiProfile == null) {
            throw new RuntimeException("Topic и AiProfile обязательны для генерации вопроса.");
        }

        return "Режим: " + aiProfile.getMode()
                + ". Сложность: " + aiProfile.getDifficulty()
                + ". Тема: " + topic.getName()
                + ". Сформулируй вопрос для собеседования.";
    }
}
