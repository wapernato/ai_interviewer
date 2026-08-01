package org.example.service.impl;

import org.example.dto.user.UserStatisticsResponse;
import org.example.exception.NotFoundException;
import org.example.repository.AnswerRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.UserRepository;
import org.example.service.UserStatisticsService;
import org.springframework.stereotype.Service;

@Service
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public UserStatisticsServiceImpl(UserRepository userRepository,
                                     QuestionRepository questionRepository,
                                     AnswerRepository answerRepository){
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public UserStatisticsResponse getUserStatistics(Long id) {

        if(!userRepository.existsById(id)){
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        }

        long totalQuestions = questionRepository.countByCreatedByUser_Id(id);
        long totalAnswers = answerRepository.countByQuestion_CreatedByUser_Id(id);
        long unansweredQuestions = totalQuestions - totalAnswers;
        double completionRate = totalQuestions == 0
                ? 0.0
                : totalAnswers * 100.0 / totalQuestions;

        return new UserStatisticsResponse(
                totalQuestions,
                totalAnswers,
                unansweredQuestions,
                completionRate
        );
    }
}
