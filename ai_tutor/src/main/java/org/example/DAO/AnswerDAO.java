package org.example.DAO;

import org.example.model.Answer;

public interface AnswerDAO {
    Answer save(Answer answer);
    Answer update(Answer answer);
    void deleteById(Long id);
}
