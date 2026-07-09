package org.example.service.impl;

import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final AiQuestionGenerator aiQuestionGenerator;
    private final AiAnswerEvaluator aiAnswerEvaluator;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final AiProfileRepository aiProfileRepository;
    private final AnswerRepository answerRepository;

    public InterviewServiceImpl(
                                AiQuestionGenerator aiQuestionGenerator,
                                AiAnswerEvaluator aiAnswerEvaluator,
                                QuestionRepository questionRepository,
                                UserRepository userRepository,
                                TopicRepository topicRepository,
                                AiProfileRepository aiProfileRepository,
                                AnswerRepository answerRepository
    ){

        this.aiQuestionGenerator = aiQuestionGenerator;
        this.aiAnswerEvaluator = aiAnswerEvaluator;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.aiProfileRepository = aiProfileRepository;
        this.answerRepository = answerRepository;
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    private Topic findTopicOrThrow(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Тема не найдена."));
    }

    private Question findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Вопрос не найден."));
    }

    private AiProfile findActiveAiProfileOrThrow() {
        return aiProfileRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new NotFoundException("Активный AI-профиль не найден."));
    }

    private String normalizeGeneratedQuestion(String questionText) {
        if (questionText == null || questionText.isBlank()) {
            throw new BadRequestException("AI не смог сгенерировать вопрос.");
        }

        return questionText.trim();
    }

    private String normalizeUserAnswer(String userAnswerText) {
        if (userAnswerText == null || userAnswerText.isBlank()) {
            throw new BadRequestException("Ответ не может быть пустой.");
        }

        return userAnswerText.trim();
    }

    @Transactional
    @Override
    public InterviewQuestionResult generateQuestion(Long userId, Long topicId){

        User user = findUserOrThrow(userId);

        Topic topic = findTopicOrThrow(topicId);

        AiProfile aiProfile = findActiveAiProfileOrThrow();

        String questionText = normalizeGeneratedQuestion(aiQuestionGenerator.generatedQuestion(topic, aiProfile));

        Question newQuestion = new Question();

        newQuestion.setUser(user);
        newQuestion.setTopic(topic);
        newQuestion.setTextQuestion(questionText);
        newQuestion.setSource("ai");
        newQuestion.setLanguage("ru");

        Question question = questionRepository.save(newQuestion);

        InterviewQuestionResult result = new InterviewQuestionResult();

        result.setQuestionId(question.getId());
        result.setUserId(user.getId());
        result.setTopicId(topic.getId());
        result.setAiProfileId(aiProfile.getId());
        result.setTopicName(topic.getName());
        result.setQuestionText(question.getTextQuestion());
        result.setAiMode(aiProfile.getMode());
        result.setDifficulty(aiProfile.getDifficulty());

        return result;
    }

    @Transactional
    @Override
    public InterviewAnswerResult submitUserAnswer(Long userId, Long questionId, String userAnswerText){

        User user = findUserOrThrow(userId);

        Question question = findQuestionOrThrow(questionId);


        if(!question.getUser().getId().equals(userId)){
            throw new BadRequestException("Нельзя ответить на вопрос другого пользователя.");
        }

        String normalizedUserAnswerText = normalizeUserAnswer(userAnswerText);

        AiProfile aiProfile = findActiveAiProfileOrThrow();

        Answer answer = new Answer();

        answer.setAnswerText(normalizedUserAnswerText);
        answer.setModelName(aiProfile.getModelName());
        answer.setQuestion(question);
        answer.setAiProfile(aiProfile);

        String feedbackText = aiAnswerEvaluator.evaluateAnswer(
                question,
                aiProfile,
                normalizedUserAnswerText
        );

        Answer savedAnswer = answerRepository.save(answer);

        InterviewAnswerResult result = new InterviewAnswerResult();

        result.setAnswerId(savedAnswer.getId());
        result.setQuestionId(questionId);
        result.setUserId(userId);
        result.setUserAnswerText(savedAnswer.getAnswerText());
        result.setQuestionText(question.getTextQuestion());
        result.setFeedback(feedbackText);

        return result;
    }
}
