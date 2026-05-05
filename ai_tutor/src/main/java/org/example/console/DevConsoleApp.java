package org.example.console;

import org.example.dto.InterviewQuestionResult;
import org.example.model.Topic;
import org.example.service.AiProfileService;
import org.example.service.InterviewService;
import org.example.service.TopicService;
import org.example.service.UserHistoryService;
import java.awt.*;
import java.util.List;
import java.util.Scanner;

public class DevConsoleApp {

    private final Scanner scanner;

    private final InterviewService interviewService;
    private final TopicService topicService;
    private final UserHistoryService userHistoryService;
    private final AiProfileService aiProfileService;

    private Long currentUserId;
    private Long lastQuestionId;

    public DevConsoleApp(InterviewService interviewService,
                         TopicService topicService,
                         UserHistoryService userHistoryService,
                         AiProfileService aiProfileService,
                         Long currentUserId) {
        this.scanner = new Scanner(System.in);
        this.interviewService = interviewService;
        this.topicService = topicService;
        this.userHistoryService = userHistoryService;
        this.aiProfileService = aiProfileService;
        this.currentUserId = currentUserId;
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();

            int command = readInt("Выберите команду: ");

            switch (command) {
                case 0 -> running = false;
                default -> System.out.println("Неизвестная команда.");
            }
        }

        System.out.println("Выход из приложения.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== AI Tutor Dev Console =====");
        System.out.println("1. Сгенерировать вопрос");
        System.out.println("2. Ответить на последний вопрос");
        System.out.println("3. Показать историю пользователя");
        System.out.println("4. Показать все темы");
        System.out.println("5. Показать активный AI-профиль");
        System.out.println("0. Выход");
    }


    public void printAllTopics(){
        List<Topic> topicList = topicService.getAllTopics();
        System.out.println("========================");
        System.out.println("ВСЕ ТЕМЫ ИЗ БАЗЫ ДАННЫХ");
        System.out.println("========================");
        System.out.println();
        for(int i = 0; i < topicList.size(); i++){
            System.out.println((i + 1) + ") " + "Название темы - " + topicList.get(i));
        }
        System.out.println();
    }

    public void generateQuestion() {


        printAllTopics();

        List<Topic> topics = topicService.getAllTopics();

        if (topics.isEmpty()) {
            System.out.println("Тем пока нет.");
            return;
        }

        int choice = readInt("Выберите номер темы: ");

        if (choice < 1 || choice > topics.size()) {
            System.out.println("Некорректный номер темы.");
            return;
        }

        Topic selectedTopic = topics.get(choice - 1);
        Long topicId = selectedTopic.getId();

        InterviewQuestionResult result = interviewService.generateQuestion(currentUserId, topicId);

        lastQuestionId = result.getQuestionId();

        System.out.println("AI сгенерировал вопрос:");
        System.out.println(result.getQuestionText());
    }

    private Long readLong() {
        Long value = scanner.nextLong();
        scanner.nextLine(); // очистить Enter после числа
        return value;
    }

    private int readInt(String message) {
        System.out.print(message);
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private String readLine(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
