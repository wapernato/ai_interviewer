package org.example.service;

import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.model.AiProfile;

public interface InterviewService {
    InterviewQuestionResult generateQuestion(Long userId, String topic);
    InterviewAnswerResult submitUserAnswer(Long userId, Long questionId, String userAnswerText);
}
