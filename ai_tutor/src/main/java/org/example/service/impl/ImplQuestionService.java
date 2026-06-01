package org.example.service.impl;

import org.example.dao.QuestionDAO;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Question;
import org.example.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplQuestionService implements QuestionService {

    private final QuestionDAO questionDAO;

    public ImplQuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    @Override
    public Question addQuestion(Long userId, Long topicId, String textQuestion) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Id пользователя должен быть положительным числом.");
        }

        if (topicId == null || topicId <= 0) {
            throw new BadRequestException("Id темы должен быть положительным числом.");
        }

        if(textQuestion == null || textQuestion.isBlank()){
            throw new BadRequestException("Текст вопроса не должен быть пустым.");
        }

        textQuestion = textQuestion.trim();

        if (textQuestion.length() < 3 || textQuestion.length() > 1000) {
            throw new BadRequestException("Длина вопроса должна быть от 3 до 1000 символов.");
        }

        Question question = new Question();

        question.setUserId(userId);
        question.setTopicId(topicId);
        question.setTextQuestion(textQuestion);
        question.setSource("manual"); // понимать откуда пришел вопрос автоматически
        question.setLanguage("ru"); // добавить определения языка по тексу

        return questionDAO.save(question);
    }

    @Override
    public Question getById(Long id) {
        Question question = questionDAO.findById(id);

        if (question == null) {
            throw new NotFoundException("Вопрос с id = " + id + " не найден.");
        }

        return question;
    }

    @Override
    public List<Question> getByTopicId(Long topicId) {
        if(topicId == null){
            throw new BadRequestException("Id темы не должен быть пустым.");
        }
        return questionDAO.findByTopicId(topicId);
    }

    @Override
    public List<Question> getByUserId(Long userId) {
        if(userId == null){
            throw new BadRequestException("Id пользователя не должен быть пустым.");
        }
        return questionDAO.findByUserId(userId);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionDAO.findAll();
    }

    @Override
    public Question updateQuestion(Long id, String newTextQuestion, String source, String language) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id вопроса должен быть положительным числом.");
        }

        if (source == null || source.isBlank()) {
            source = "manual";
        } else {
            source = source.trim();
        }

        if (language == null || language.isBlank()) {
            language = "ru";
        } else {
            language = language.trim();
        }

        Question oldQuestion = questionDAO.findById(id);

        if (oldQuestion == null) {
            throw new NotFoundException("Вопрос с id = " + id + " не найден.");
        }

        if (newTextQuestion == null || newTextQuestion.isBlank()) {
            throw new BadRequestException("Новый текст вопроса не должен быть пустым.");
        }

        newTextQuestion = newTextQuestion.trim();

        if (newTextQuestion.length() < 3 || newTextQuestion.length() > 1000) {
            throw new BadRequestException("Длина вопроса должна быть от 3 до 1000 символов.");
        }

        String oldText = oldQuestion.getTextQuestion().trim();

        if(newTextQuestion.equals(oldText)){
            throw new BadRequestException("Текст не должен совпадать со старым текстом.");
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
            throw new BadRequestException("Id вопроса должен быть положительным числом.");
        }

        Question question = questionDAO.findById(id);

        if (question == null) {
            throw new NotFoundException("Вопрос с id = " + id + " не найден.");
        }

        questionDAO.deleteById(id);
    }
}