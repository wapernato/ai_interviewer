package org.example.repository;

import org.example.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestion_Id(Long answerId);

    List<Answer> findByAiProfile_Id(Long aiProfileId);

}
