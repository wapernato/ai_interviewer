package org.example.service;

import org.example.model.AiProfile;
import org.example.model.Answer;
import org.example.model.Question;

public interface AiAnswerEvaluator {
    String evaluateAnswer(Question question, AiProfile aiProfile, String userAnswerText);
}
