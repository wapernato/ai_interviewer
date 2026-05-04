package org.example;

import org.example.dao.*;
import org.example.dao.impl.*;
import org.example.DBConnection.DBConnection;
import org.example.dto.UserHistoryItem;
import org.example.model.*;
import org.example.service.*;
import org.example.service.impl.*;

import java.util.List;
import java.io.IOException;

import java.sql.*;


public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        DBConnection dbConnection = new DBConnection();

        UserDAO userDAO = new ImplUserDAO(dbConnection);
        TopicDAO topicDAO = new ImplTopicDAO(dbConnection);
        QuestionDAO questionDAO = new ImplQuestionDAO(dbConnection);
        AnswerDAO answerDAO = new ImplAnswerDAO(dbConnection);
       AiProfileDAO aiProfileDAO = new ImplAiProfileDAO(dbConnection);

        UserService userService = new ImplUserService(userDAO);
        TopicService topicService = new ImplTopicService(topicDAO);
        QuestionService questionService = new ImplQuestionService(questionDAO);
        AnswerService answerService = new ImplAnswerService(answerDAO);
        AiProfileService aiProfileService = new ImplAiProfileService(aiProfileDAO);
        UserHistoryDAO userHistoryDAO = new ImplUserHistoryDAO(dbConnection);

        UserHistoryService userHistoryService = new ImpldUserHistoryService(userHistoryDAO, userDAO);

//        User user = userService.register("Ignazem");
//
//        Topic topic = topicService.addTopic("Greedy");
//
//        AiProfile aiProfile = aiProfileService.addAiProfile(
//                "hard_interviewer",
//                "Строгий технический интервьюер",
//                "Ты проводишь жёсткое техническое собеседование по Java.",
//                "ru",
//                "strict",
//                true,
//                true);
//
//        Question question = questionService.addQuestion(
//                user.getId(),
//                topic.getId(),
//                "Что такое инкапсуляция в Java?"
//        );
//
//
//        Answer answer = answerService.addAnswer(
//                question.getId(),
//                aiProfile.getId(),
//                "Инкапсуляция — это принцип ООП, при котором данные объекта скрываются...",
//                "gpt-5.5"
//        );
//
//        System.out.println(user);
//        System.out.println(topic);
//        System.out.println(aiProfile);
//        System.out.println(question);
//        System.out.println(answer);

        try {
            Long userId = 1L;

            List<UserHistoryItem> history = userHistoryService.findHistoryByUserId(userId);

            if (history.isEmpty()) {
                System.out.println("История пользователя пустая.");
            } else {
                for (UserHistoryItem item : history) {
                    System.out.println(item);
                }
            }

        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }



}