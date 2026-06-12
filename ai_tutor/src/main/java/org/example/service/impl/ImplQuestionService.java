package org.example.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Question;
import org.example.model.Topic;
import org.example.model.User;
import org.example.repository.QuestionRepository;
import org.example.repository.TopicRepository;
import org.example.repository.UserRepository;
import org.example.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImplQuestionService implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public ImplQuestionService(QuestionRepository questionRepository
                               ,TopicRepository topicRepository
                               ,UserRepository userRepository
    ) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Transactional
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(()-> new BadRequestException("Тема с таким id=" + topicId + " не существует."));



        Question question = new Question();

        question.setTopic(topic);
        question.setUser(user);
        question.setTextQuestion(textQuestion);
        question.setSource("manual");
        question.setLanguage("ru");

        return questionRepository.save(question);
    }

    @Transactional(readOnly = true)
    @Override
    public Question getById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id должен быть положительным числом.");
        }
        Optional<Question> question = questionRepository.findById(id);

        return questionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Вопрос с таким id = " + id + " не найден."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Question> getByTopicId(Long topicId) {
        if(topicId == null){
            throw new BadRequestException("Id темы не должен быть пустым.");
        }
        //реализовать в репозитории поиск по id пользователя и id темы
        return topicRepository.findByTopic_Id(topicId)
                .orElseThrow(() -> new BadRequestException("Тема с таким id = " + topicId + " не найден."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Question> getByUserId(Long userId) {
        if(userId == null){
            throw new BadRequestException("Id пользователя не должен быть пустым.");
        }
        return questionRepository.findById(userId)
                .orElseThrow(() -> BadRequestException());
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