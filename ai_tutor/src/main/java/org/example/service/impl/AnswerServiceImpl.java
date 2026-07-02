package org.example.service.impl;

import org.example.dto.response.AnswerResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.AnswerMapper;
import org.example.model.AiProfile;
import org.example.model.Answer;
import org.example.model.Question;
import org.example.repository.AiProfileRepository;
import org.example.repository.AnswerRepository;
import org.example.repository.QuestionRepository;
import org.example.service.AnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AiProfileRepository aiProfileRepository;
    private final AnswerMapper answerMapper;

    public AnswerServiceImpl(AnswerRepository answerRepository
                            ,QuestionRepository questionRepository
                            ,AiProfileRepository aiProfileRepository
                            ,AnswerMapper answerMapper
    ) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.aiProfileRepository = aiProfileRepository;
        this.answerMapper = answerMapper;
    }

    private void validateId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new BadRequestException(message);
        }
    }

    private String normalizeRequiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(message);
        }

        return value.trim();
    }

    private Question findQuestionOrThrow(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Вопрос с id=" + questionId + " не найден."));
    }

    private AiProfile findAiProfileOrThrow(Long aiProfileId) {
        return aiProfileRepository.findById(aiProfileId)
                .orElseThrow(() -> new NotFoundException("AI-профиль с id=" + aiProfileId + " не найден."));
    }

    private Answer findAnswerOrThrow(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ответ с id=" + id + " не найден."));
    }

    @Transactional
    @Override
    public AnswerResponse addAnswer(Long questionId, Long aiProfileId, String answerText, String modelName) {
        validateId(questionId, "Id вопроса не может быть null.");
        validateId(aiProfileId, "Id профиля не может быть null.");

        String normalizedAnswerText = normalizeRequiredText(answerText, "Текст ответа не может быть пустым.");
        String normalizedModelName = normalizeRequiredText(modelName, "Название модели не может быть пустым.");

        Answer answer = new Answer();

        Question question = findQuestionOrThrow(questionId);
        AiProfile aiProfile = findAiProfileOrThrow(aiProfileId);

        answer.setQuestion(question);
        answer.setAiProfile(aiProfile);
        answer.setAnswerText(normalizedAnswerText);
        answer.setModelName(normalizedModelName);

        Answer savedAnswer = answerRepository.save(answer);

        return answerMapper.toResponse(savedAnswer);
    }

    @Transactional
    @Override
    public AnswerResponse updateAnswer(Long id, String answerText, String modelName) {
        validateId(id, "Id ответа не может быть null.");

        String normalizedAnswerText = normalizeRequiredText(answerText, "Текст ответа не может быть пустым.");
        String normalizedModelName = normalizeRequiredText(modelName, "Название модели не может быть пустым.");

        Answer existingAnswer = findAnswerOrThrow(id);

        existingAnswer.setAnswerText(normalizedAnswerText);
        existingAnswer.setModelName(normalizedModelName);

        Answer savedAnswer =  answerRepository.save(existingAnswer);

        return answerMapper.toResponse(savedAnswer);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        validateId(id, "Id ответа не может быть null.");

        findAnswerOrThrow(id);

        answerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public AnswerResponse getById(Long id) {
        validateId(id, "Id ответа не может быть null.");

        Answer savedAnswer = findAnswerOrThrow(id);

        return answerMapper.toResponse(savedAnswer);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AnswerResponse> getByQuestionId(Long questionId) {
        validateId(questionId, "Id вопроса не может быть null.");

        if(!questionRepository.existsById(questionId)){
            throw new NotFoundException("Вопрос с id=" + questionId + " не найден.");
        }

        List<Answer> savedAnswerList = answerRepository.findByQuestion_Id(questionId);

        return answerMapper.toResponseList(savedAnswerList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AnswerResponse> getByProfileId(Long profileId) {
        validateId(profileId, "Id AI-профиля не может быть null.");

        if(!aiProfileRepository.existsById(profileId)){
            throw new NotFoundException("AI-профиль с id=" + profileId + " не найден.");
        }

        List<Answer> savedAnswerList = answerRepository.findByAiProfile_Id(profileId);

        return answerMapper.toResponseList(savedAnswerList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AnswerResponse> getAllAnswers() {

        List<Answer> savedAnswerList = answerRepository.findAll();
        return answerMapper.toResponseList(savedAnswerList);
    }
}
