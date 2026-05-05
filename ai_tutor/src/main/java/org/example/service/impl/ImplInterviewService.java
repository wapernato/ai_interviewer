package org.example.service.impl;

import org.example.dto.InterviewAnswerResult;
import org.example.dto.InterviewQuestionResult;
import org.example.model.*;
import org.example.service.*;

public class ImplInterviewService implements InterviewService {

    private final UserService userService;
    private final TopicService topicService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final AiProfileService aiProfileService;

    public ImplInterviewService(UserService userService,
                                TopicService topicService,
                                QuestionService questionService,
                                AnswerService answerService,
                                AiProfileService aiProfileService ){
        this.topicService = topicService;
        this.userService = userService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.aiProfileService =  aiProfileService;
    };


    @Override
    public InterviewQuestionResult generateQuestion(Long userId, Long topicId){

        User user = userService.getById(userId);

        Topic topic = topicService.getByTopicId(topicId);

        AiProfile aiProfile = aiProfileService.getActiveProfile();

        String questionText = "ГЕНЕРАЦИЯ ВОПРОСА ЧЕРЕЗ AI" + topic.getName();

        Question question = questionService.addQuestion(
                user.getId(),
                topic.getId(),
                questionText
        );

        InterviewQuestionResult result = new InterviewQuestionResult();

        result.setQuestionId(question.getId());
        result.setUserId(user.getId());
        result.setTopicId(topic.getId());
        result.setAiProfileId(aiProfile.getId());
        result.setQuestionText(question.getTextQuestion());
        result.setAiMode(aiProfile.getMode());
        result.setDifficulty(aiProfile.getDifficulty());

        return result;
    }

    @Override
    public InterviewAnswerResult submitUserAnswer(Long userId, Long questionId, String userAnswerText){

        User user = userService.getById(userId);

        Question question = questionService.getById(questionId);



        if(!question.getUserId().equals(userId)){
            throw new RuntimeException("Нельзя ответить на вопрос другого пользователя.");
        }

        if(userAnswerText == null || userAnswerText.isBlank()){
            throw new RuntimeException("Ответ не может быть пустой.");
        }

        AiProfile aiProfile = aiProfileService.getActiveProfile();

        Answer answer = answerService.addAnswer(
                question.getId(),
                aiProfile.getId(),
                userAnswerText.trim(),
                "user-input"
        );


        InterviewAnswerResult result = new InterviewAnswerResult();

        result.setAnswerId(answer.getId());
        result.setQuestionId(questionId);
        result.setUserId(userId);
        result.setUserAnswerText(userAnswerText);
        result.setQuestionText(question.getTextQuestion());

        return result;
    }
}
