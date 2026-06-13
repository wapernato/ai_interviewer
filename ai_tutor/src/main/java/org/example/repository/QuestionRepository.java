package org.example.repository;

import org.example.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopic_Id(Long topicId);

    List<Question> findByUser_Id(Long userId);
}