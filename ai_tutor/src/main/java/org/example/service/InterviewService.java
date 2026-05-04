package org.example.service;

import org.example.dto.InterviewAnswerResult;
import org.example.dto.InterviewQuestionResult;

public interface InterviewService {
    InterviewQuestionResult generateQuestion(Long userId, Long topicId);
    InterviewAnswerResult submitUserAnswer(Long userId, Long questionId, String userAnswerText);
}
