package org.example.service.impl;

import org.example.dto.ai.QuestionGenerationResult;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.integration.ai.AiQuestionGenerator;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    private Question findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Вопрос не найден."));
    }

    private Topic findOrCreateTopic(String topic) {
        Optional<Topic> savedTopic = topicRepository.findByName(topic);
        if(savedTopic.isPresent()){
            return savedTopic.get();
        }
        Topic newTopic = new Topic(topic);
        return topicRepository.save(newTopic);
    }

    private String normalizeUserAnswer(String userAnswerText) {
        if (userAnswerText == null || userAnswerText.isBlank()) {
            throw new BadRequestException("Ответ не может быть пустой.");
        }

        return userAnswerText.trim();
    }

    private InterviewQuestionResult createInterviewQuestionResult(Question question) {
        InterviewQuestionResult interviewQuestionResult = new InterviewQuestionResult();
        interviewQuestionResult.setQuestionText(question.getTextQuestion());
        interviewQuestionResult.setDifficulty(question.getDifficulty());
        interviewQuestionResult.setAiMode(question.getAiProfile().getMode());
        interviewQuestionResult.setTopicName(question.getTopic().getName());
        interviewQuestionResult.setQuestionId(question.getId());
        interviewQuestionResult.setUserId(question.getCreatedByUser().getId());
        interviewQuestionResult.setTopicId(question.getTopic().getId());
        interviewQuestionResult.setAiProfileId(question.getAiProfile().getId());

        return interviewQuestionResult;
    }

    @Transactional
    @Override
    public InterviewQuestionResult generateQuestion(Long userId, Long aiProfileId, String requestedTopic){
        User user = findUserOrThrow(userId);

        AiProfile aiProfile = aiProfileRepository.findById(aiProfileId).orElseThrow(() -> new NotFoundException("AI-профиль не найден."));

        if(!Boolean.TRUE.equals(aiProfile.getActive())){
            throw new BadRequestException("Данный профиль недоступен.");
        }

        QuestionGenerationResult generationResult = aiQuestionGenerator.generate(requestedTopic, aiProfile);

        Topic topic = findOrCreateTopic(generationResult.getNormalizedTopic());

        Question question = new Question();

        question.setCreatedByUser(user);
        question.setAiProfile(aiProfile);
        question.setTopic(topic);
        question.setTextQuestion(generationResult.getQuestionText());
        question.setDifficulty(generationResult.getDifficulty());
        // ======================
        // ЭТИ ДАННЫЕ НАДО БУДЕТ ПОЛУЧАТЬ ОТ ИИ
        question.setSource("ai");
        question.setLanguage("ru");
        // ======================
        Question savedQuestion = questionRepository.save(question);

        return createInterviewQuestionResult(savedQuestion);
    }

    @Transactional
    @Override
    public InterviewAnswerResult submitUserAnswer(Long userId, Long questionId, String userAnswerText){

        User user = findUserOrThrow(userId);

        Question question = findQuestionOrThrow(questionId);


        if(!question.getCreatedByUser().getId().equals(userId)){
            throw new BadRequestException("Нельзя ответить на вопрос другого пользователя.");
        }

        String normalizedUserAnswerText = normalizeUserAnswer(userAnswerText);

        AiProfile aiProfile = question.getAiProfile();

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
