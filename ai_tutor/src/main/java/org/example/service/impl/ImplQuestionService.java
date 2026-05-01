package org.example.service.impl;

import org.example.DAO.QuestionDAO;
import org.example.model.Question;
import org.example.service.QuestionService;

import java.util.List;

public class ImplQuestionService implements QuestionService {

    private final QuestionDAO questionDAO;

    public ImplQuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    @Override
    public Question addQuestion(Long userId, Long topicId, String textQuestion) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Id пользователя должен быть положительным числом.");
        }

        if (topicId == null || topicId <= 0) {
            throw new IllegalArgumentException("Id темы должен быть положительным числом.");
        }

        if(textQuestion == null || textQuestion.isBlank()){
            throw new IllegalArgumentException("Текст вопроса не должен быть пустым.");
        }

        textQuestion = textQuestion.trim();

        if (textQuestion.length() < 3 || textQuestion.length() > 1000) {
            throw new IllegalArgumentException("Длина вопроса должна быть от 3 до 1000 символов.");
        }

        Question question = new Question();

        question.setUserId(userId);
        question.setTopicId(topicId);
        question.setTextQuestion(textQuestion);
        question.setSource("manual"); // понимать откуда пришел вопрос автоматически\
        question.setLanguage("ru"); // добавить определения языка по тексу

        return questionDAO.save(question);
    }

    @Override
    public Question getById(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Id вопроса не должен быть пустым.");
        }
        return questionDAO.findById(id);
    }

    @Override
    public List<Question> getByTopicId(Long topicId) {
        if(topicId == null){
            throw new IllegalArgumentException("Id темы не должен быть пустым.");
        }
        return questionDAO.findByTopicId(topicId);
    }

    @Override
    public List<Question> getByUserId(Long userId) {
        if(userId == null){
            throw new IllegalArgumentException("Id пользователя не должен быть пустым.");
        }
        return questionDAO.findByUserId(userId);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionDAO.findAll();
    }

    @Override
    public Question updateQuestion(Long id, String newTextQuestion, String source, String language) {
        source = source.trim();
        language = language.trim();

        if(id == null){
            throw new IllegalArgumentException("Id вопроса не должен быть пустым.");
        }
        Question oldQuestion = questionDAO.findById(id);

        if (oldQuestion == null) {
            throw new RuntimeException("Вопрос с id = " + id + " не найден.");
        }

        if (newTextQuestion == null || newTextQuestion.isBlank()) {
            throw new IllegalArgumentException("Новый текст вопроса не должен быть пустым.");
        }

        newTextQuestion = newTextQuestion.trim();

        String oldText = oldQuestion.getTextQuestion().trim();

        if(newTextQuestion.equals(oldText)){
            throw new RuntimeException("Текст не должен совпадать со старым текстом.");
        }


        if (source == null || source.isBlank()) {
            source = "manual";
        }

        if (language == null || language.isBlank()) {
            language = "ru";
        }

        // добавить потом проверку существует ли такой язык
        oldQuestion.setLanguage(language);
        oldQuestion.setTextQuestion(newTextQuestion);
        oldQuestion.setSource(source);

        return questionDAO.update(oldQuestion);
    }

    @Override
    public void deleteById(Long id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("Id вопроса должен быть положительным числом.");
        }

        questionDAO.deleteById(id);
    }
}