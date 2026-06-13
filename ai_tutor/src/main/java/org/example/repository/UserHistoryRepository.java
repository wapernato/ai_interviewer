package org.example.repository;

import org.example.dto.user.UserHistoryItem;
import org.example.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHistoryRepository extends JpaRepository<Question, Long> {
    @Query("""
        select new org.example.dto.user.UserHistoryItem(
            q.id,
            u.username,
            t.name,
            q.textQuestion,
            a.answerText,
            a.modelName
        )
        from Question q
        join q.user u
        left join q.topic t
        left join Answer a on a.question = q
        where u.id = :userId
        order by q.id asc
        """)
    List<UserHistoryItem> findHistoryByUserId(@Param("userId") Long userId);
}
