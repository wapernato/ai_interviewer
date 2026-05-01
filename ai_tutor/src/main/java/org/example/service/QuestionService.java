package org.example.service;

import org.example.model.Question;

import java.util.List;

public interface QuestionService {

    Question addQuestion(Long userId, Long topicId, String textQuestion);
    List<Question> getAllQuestions();

    Question getById(Long id);
    List<Question> getByTopicId(Long topicId);
    List<Question> getByUserId(Long userId);

    Question updateQuestion(Long id, String newTextQuestion, String source, String language);
    void deleteById(Long id);
}