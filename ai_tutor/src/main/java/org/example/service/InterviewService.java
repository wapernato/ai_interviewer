package org.example.service;

import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;

public interface InterviewService {
    InterviewQuestionResult generateQuestion(Long userId, Long topicId);
    InterviewAnswerResult submitUserAnswer(Long userId, Long questionId, String userAnswerText);
}
