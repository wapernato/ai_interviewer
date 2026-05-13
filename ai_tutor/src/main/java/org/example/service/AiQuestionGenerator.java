package org.example.service;

import org.example.model.AiProfile;
import org.example.model.Topic;

public interface AiQuestionGenerator {
    String generatedQuestion(Topic topic, AiProfile aiProfile);
}
