package org.example.service.impl;

import org.example.dto.response.QuestionResponse;
import org.example.mapper.QuestionMapper;
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

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TopicRepository topicRepository,
                               UserRepository userRepository,
                               QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.questionMapper = questionMapper;
    }

    @Transactional
    @Override
    public QuestionResponse addQuestion(Long userId, Long topicId, String textQuestion) {
        if(textQuestion == null || textQuestion.isBlank()){
            throw new BadRequestException("Текст вопроса не может быть пустым.");
        }

        String normalizedTextQuestion = textQuestion.trim();

        if (normalizedTextQuestion.length() < 3 || normalizedTextQuestion.length() > 1000) {
            throw new BadRequestException("Длина вопроса должна быть от 3 до 1000 символов.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(()-> new NotFoundException("Тема с таким id=" + topicId + " не существует."));

        Question question = new Question();

        question.setTopic(topic);
        question.setUser(user);
        question.setTextQuestion(normalizedTextQuestion);
        question.setSource("manual");
        question.setLanguage("ru");


        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Transactional(readOnly = true)
    @Override
    public QuestionResponse getById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вопрос с таким id = " + id + " не найден."));
        return questionMapper.toResponse(question);
    }

    @Transactional(readOnly = true)
    @Override
    public List<QuestionResponse> getByTopicId(Long topicId) {
        topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Тема с id=" + topicId + " не найдена."));

        return questionMapper.toResponseList(questionRepository.findByTopic_Id(topicId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<QuestionResponse> getByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));

        return questionMapper.toResponseList(questionRepository.findByUser_Id(userId));
    }

    @Transactional(readOnly = true)
    @Override
    public List<QuestionResponse> getAllQuestions() {
        List<Question> questionList = questionRepository.findAll();
        return questionMapper.toResponseList(questionList);
    }

    @Transactional
    @Override
    public QuestionResponse updateQuestion(Long id, String newTextQuestion, String source, String language) {

        if(newTextQuestion == null || newTextQuestion.isBlank()){
            throw new BadRequestException("Новый текст вопроса не может быть пустым.");
        }

        if(source == null || source.isBlank()){
            throw new BadRequestException("Source вопроса не может быть пустым.");
        }

        if(language == null || language.isBlank()){
            throw new BadRequestException("Language вопроса не может быть пустым.");
        }

        newTextQuestion = newTextQuestion.trim();
        source = source.trim();
        language = language.trim();

        Question oldQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вопрос с id = " + id + " не найден."));


        if (newTextQuestion.length() < 3 || newTextQuestion.length() > 1000) {
            throw new BadRequestException("Длина вопроса должна быть от 3 до 1000 символов.");
        }

        String oldText = oldQuestion.getTextQuestion().trim();

        if(newTextQuestion.equals(oldText)){
            throw new BadRequestException("Текст не должен совпадать со старым текстом.");
        }


        oldQuestion.setLanguage(language);
        oldQuestion.setTextQuestion(newTextQuestion);
        oldQuestion.setSource(source);

        Question question = questionRepository.save(oldQuestion);

        return questionMapper.toResponse(question);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Указанный Id=" + id + " не найден в базе данных."));

        questionRepository.deleteById(id);
    }
}
