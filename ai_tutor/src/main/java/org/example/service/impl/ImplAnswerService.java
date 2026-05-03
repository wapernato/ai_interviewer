package org.example.service.impl;

import org.example.DAO.AnswerDAO;
import org.example.model.Answer;
import org.example.service.AnswerService;

import java.util.List;

public class ImplAnswerService implements AnswerService {

    private final AnswerDAO answerDAO;

    public ImplAnswerService(AnswerDAO answerDAO) {
        this.answerDAO = answerDAO;
    }

    @Override
    public Answer addAnswer(Long questionId, Long aiProfileId, String answerText, String modelName) {
        if (questionId == null) {
            throw new RuntimeException("Id вопроса не может быть null.");
        }

        if (answerText == null || answerText.isBlank()) {
            throw new RuntimeException("Текст ответа не может быть пустым.");
        }

        if (modelName == null || modelName.isBlank()) {
            throw new RuntimeException("Название модели не может быть пустым.");
        }

        Answer answer = new Answer();
        answer.setQuestionId(questionId);
        answer.setAiProfileId(aiProfileId);
        answer.setAnswerText(answerText.trim());
        answer.setModelName(modelName.trim());

        return answerDAO.save(answer);
    }

    @Override
    public Answer updateAnswer(Long id, String answerText, String modelName) {
        if (id == null) {
            throw new RuntimeException("Id ответа не может быть null.");
        }

        if (answerText == null || answerText.isBlank()) {
            throw new RuntimeException("Текст ответа не может быть пустым.");
        }

        if (modelName == null || modelName.isBlank()) {
            throw new RuntimeException("Название модели не может быть пустым.");
        }

        Answer existingAnswer = answerDAO.findById(id);

        if (existingAnswer == null) {
            throw new RuntimeException("Ответ с таким id не найден.");
        }

        existingAnswer.setAnswerText(answerText.trim());
        existingAnswer.setModelName(modelName.trim());

        return answerDAO.update(existingAnswer);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new RuntimeException("Id ответа не может быть null.");
        }

        Answer existingAnswer = answerDAO.findById(id);

        if (existingAnswer == null) {
            throw new RuntimeException("Ответ с таким id не найден.");
        }

        answerDAO.deleteById(id);
    }

    @Override
    public Answer getById(Long id) {
        if (id == null) {
            throw new RuntimeException("Id ответа не может быть null.");
        }

        Answer answer = answerDAO.findById(id);

        if (answer == null) {
            throw new RuntimeException("Ответ с таким id не найден.");
        }

        return answer;
    }

    @Override
    public List<Answer> getByQuestionId(Long questionId) {
        if (questionId == null) {
            throw new RuntimeException("Id вопроса не может быть null.");
        }

        return answerDAO.findByQuestionId(questionId);
    }

    @Override
    public List<Answer> getByProfileId(Long profileId) {
        if (profileId == null) {
            throw new RuntimeException("Id AI-профиля не может быть null.");
        }

        return answerDAO.findByProfileId(profileId);
    }

    @Override
    public List<Answer> getAllAnswers() {
        return answerDAO.findAll();
    }
}