package org.example.DAO;

import org.example.model.Answer;

import java.util.List;

public interface AnswerDAO {
    List<Answer> findByQuestionId(Long questionId);
    List<Answer> findByProfileId(Long profileId);
    Answer findById(Long id);
    List<Answer> findAll();

    Answer save(Answer answer);
    Answer update(Answer answer);
    void deleteById(Long id);
}
