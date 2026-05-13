package org.example.service.impl;

import org.example.model.AiProfile;
import org.example.model.Answer;
import org.example.model.Question;
import org.example.service.AiAnswerEvaluator;
import org.springframework.stereotype.Service;

@Service
public class ImplAiAnswerEvaluator implements AiAnswerEvaluator {

    @Override
    public String evaluateAnswer(Question question, AiProfile aiProfile, String userAnswerText) {

        return "Ответ принят. Предварительная AI-оценка: ответ связан с вопросом, но пока проверка работает в mock-режиме. "
                + "Рекомендуется раскрыть определение подробнее, привести пример из практики и объяснить, почему это важно для backend-разработки. "
                + "Режим проверки: " + aiProfile.getMode()
                + ", сложность: " + aiProfile.getDifficulty()
                + ".";
    }
}
