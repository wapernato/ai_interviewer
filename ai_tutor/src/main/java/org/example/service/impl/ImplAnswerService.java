package org.example.service.impl;

import org.example.exception.AnswerAlreadyExistsException;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
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
public class ImplAnswerService implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AiProfileRepository aiProfileRepository;

    public ImplAnswerService(AnswerRepository answerRepository
                            ,QuestionRepository questionRepository
                            ,AiProfileRepository aiProfileRepository
    ) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.aiProfileRepository = aiProfileRepository;
    }

    @Transactional
    @Override
    public Answer addAnswer(Long questionId, Long aiProfileId, String answerText, String modelName) {
        if (questionId == null || questionId <= 0) {
            throw new BadRequestException("Id вопроса не может быть null.");
        }

        if (aiProfileId == null || aiProfileId <= 0) {
            throw new BadRequestException("Id профиля не может быть null.");
        }

        if (answerText == null || answerText.isBlank()) {
            throw new BadRequestException("Текст ответа не может быть пустым.");
        }

        if (modelName == null || modelName.isBlank()) {
            throw new BadRequestException("Название модели не может быть пустым.");
        }

        Answer answer = new Answer();
        Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new NotFoundException("Вопрос с id=" + questionId + " не найден."));
        AiProfile aiProfile = aiProfileRepository.findById(aiProfileId)
                        .orElseThrow(() -> new NotFoundException("AI-профиль с id=" + aiProfileId + " не найден."));

        answer.setQuestion(question);
        answer.setAiProfile(aiProfile);
        answer.setAnswerText(answerText.trim());
        answer.setModelName(modelName.trim());

        return answerRepository.save(answer);
    }

    @Transactional
    @Override
    public Answer updateAnswer(Long id, String answerText, String modelName) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id ответа не может быть null.");
        }

        if (answerText == null || answerText.isBlank()) {
            throw new BadRequestException("Текст ответа не может быть пустым.");
        }

        if (modelName == null || modelName.isBlank()) {
            throw new BadRequestException("Название модели не может быть пустым.");
        }

        Answer existingAnswer = answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ответ с id=" + id + " не найден."));

        existingAnswer.setAnswerText(answerText.trim());
        existingAnswer.setModelName(modelName.trim());

        return answerRepository.save(existingAnswer);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id ответа не может быть null.");
        }

       answerRepository.findById(id)
               .orElseThrow(() -> new NotFoundException("Ответ с id=" + id + " не найден."));


        answerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Answer getById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id ответа не может быть null.");
        }

        return answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ответ с id=" + id + " не найден."));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Answer> getByQuestionId(Long questionId) {
        if (questionId == null || questionId <= 0) {
            throw new BadRequestException("Id вопроса не может быть null.");
        }

        return answerRepository.findByQuestion_Id(questionId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Answer> getByProfileId(Long profileId) {
        if (profileId == null || profileId <= 0) {
            throw new BadRequestException("Id AI-профиля не может быть null.");
        }

        return answerRepository.findByAiProfile_Id(profileId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }
}