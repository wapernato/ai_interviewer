package org.example.service;

import org.example.dto.response.question.QuestionResponse;

import java.util.List;

public interface QuestionService {

    QuestionResponse addQuestion(Long userId, Long topicId, String textQuestion);
    List<QuestionResponse> getAllQuestions();

    QuestionResponse getById(Long id);
    List<QuestionResponse> getByTopicId(Long topicId);
    List<QuestionResponse> getByUserId(Long userId);

    QuestionResponse updateQuestion(Long id, String newTextQuestion, String source, String language);
    void deleteById(Long id);
}