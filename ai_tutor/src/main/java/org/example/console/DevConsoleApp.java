package org.example.console;

import org.example.dto.interview.InterviewQuestionResult;
import org.example.model.Topic;
import org.example.service.AiProfileService;
import org.example.service.InterviewService;
import org.example.service.TopicService;
import org.example.service.UserHistoryService;
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
    private String lastQuestionText;


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

            try {
                switch (command) {
                    case 1 -> generateQuestion();
                    case 2 -> answerQuestion();
                    case 3 -> readUserHistory();
                    case 4 -> printAllTopics();
                    case 5 -> showActiveAi();
                    case 0 -> running = false;
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
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
            System.out.println((i + 1) + ") " + topicList.get(i).getName());
        }
        System.out.println();
    }

    public void generateQuestion() {

        List<Topic> topics = topicService.getAllTopics();

        if (topics.isEmpty()) {
            System.out.println("Тем пока нет.");
            return;
        }

        for (int i = 0; i < topics.size(); i++) {
            System.out.println((i + 1) + ") " + topics.get(i).getName());
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
        lastQuestionText = result.getQuestionText();
        System.out.println("AI сгенерировал вопрос:");
        System.out.println(result.getQuestionText());
    }

    public void answerQuestion(){
        if (currentUserId == null || currentUserId <= 0) {
            throw new RuntimeException("Id пользователя указан некорректно.");
        }

        if (lastQuestionId == null || lastQuestionId <= 0) {
            System.out.println("Вопрос еще не был создан.");
            return;
        }

        System.out.println();
        System.out.println("Вопрос:");
        System.out.println(lastQuestionText);
        System.out.println();

        String ans = readLine("Ответьте на вопрос: ");

        if (ans == null || ans.isBlank()) {
            System.out.println("Ответ не может быть пустым.");
            return;
        }

        var result = interviewService.submitUserAnswer(currentUserId, lastQuestionId, ans);
        System.out.println("Ответ сохранён.");

        System.out.println(result);

        lastQuestionId = null;
        lastQuestionText = null;
    }

    public void readUserHistory(){
        if (currentUserId == null || currentUserId <= 0) {
            throw new RuntimeException("Id пользователя указан некорректно.");
        }

        var history = userHistoryService.findHistoryByUserId(currentUserId);

        if (history.isEmpty()) {
            System.out.println("История пользователя пустая.");
            return;
        }

        for (var item : history) {
            System.out.println(item);
        }
    }

    public void showActiveAi(){
        try {
            System.out.println(aiProfileService.getActiveProfile());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

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
