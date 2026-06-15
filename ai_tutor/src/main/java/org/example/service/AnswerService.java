package org.example.service;

import org.example.dto.response.AnswerResponse;

import java.util.List;

public interface AnswerService {
    AnswerResponse addAnswer(Long questionId, Long aiProfileId, String answerText, String modelName);
    AnswerResponse updateAnswer(Long id, String answerText, String modelName);

    AnswerResponse getById(Long id);
    List<AnswerResponse> getByQuestionId(Long questionId);
    List<AnswerResponse> getByProfileId(Long profileId);
    List<AnswerResponse> getAllAnswers();

    void deleteById(Long id);
}
