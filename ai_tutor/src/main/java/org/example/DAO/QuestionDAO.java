package org.example.DAO;

import org.example.model.Question;
import java.util.List;

public interface QuestionDAO {
    List<Question> findByUserId(Long userId);
    List<Question> findByTopicId(Long topicId);
    Question findById(Long id);
    List<Question> findAll();

    Question save(Question question);

    Question update(Question question);
    void deleteById(Long id);
}
