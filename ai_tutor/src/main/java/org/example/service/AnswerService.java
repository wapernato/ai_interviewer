package org.example.service;

import org.example.model.Answer;

import java.util.List;

public interface AnswerService {
    Answer addAnswer(Long questionId, Long aiProfileId, String answerText, String modelName);
    Answer updateAnswer(Long id, String answerText, String modelName);

    Answer getById(Long id);
    List<Answer> getByQuestionId(Long questionId);
    List<Answer> getByProfileId(Long profileId);
    List<Answer> getAllAnswers();

    void deleteById(Long id);
}
