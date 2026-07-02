package org.example.repository;

import org.example.dto.user.UserHistoryItem;
import org.example.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserHistoryRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired UserHistoryRepository userHistoryRepository;
    @Autowired UserRepository userRepository;
    @Autowired TopicRepository topicRepository;
    @Autowired QuestionRepository questionRepository;
    @Autowired AnswerRepository answerRepository;

    private Question createQuestion(User user, Topic topic, String text) {
        Question question = new Question();

        question.setUser(user);
        question.setTopic(topic);
        question.setTextQuestion(text);
        question.setSource("manual");
        question.setLanguage("ru");

        questionRepository.save(question);

        return question;
    }

    private void createAnswer(Question question) {
        Answer answer = new Answer();

        answer.setQuestion(question);
        answer.setAnswerText("JVM executes bytecode.");
        answer.setModelName("mock-ai");
        answerRepository.save(answer);
    }

    @Test
    void findHistoryByUserId_shouldReturnHistory_whenDataIsValid() {
        User user1 = userRepository.save(new User("Yakov"));
        User user2 = userRepository.save(new User("Rodion"));

        Topic topic1 = topicRepository.save(new Topic("Java Core"));
        Topic topic2 = topicRepository.save(new Topic("Spring"));

        Question question1 = createQuestion(user1, topic1, "What is JVM?");
        Question question2 = createQuestion(user1, topic2, "What is DI?");
        Question question3 = createQuestion(user2, topic2, "What is Spring?");

        createAnswer(question1);
        createAnswer(question3);

        List<UserHistoryItem> historyUser1 = userHistoryRepository.findHistoryByUserId(user1.getId());
        List<UserHistoryItem> historyUser2 = userHistoryRepository.findHistoryByUserId(user2.getId());

        assertThat(historyUser1).hasSize(2);
        assertThat(historyUser2).hasSize(1);

        assertThat(historyUser1)
                .extracting(UserHistoryItem::getUsername)
                .containsExactly("Yakov", "Yakov");

        assertThat(historyUser1)
                .extracting(UserHistoryItem::getTopicName)
                .containsExactly("Java Core", "Spring");

        assertThat(historyUser2)
                .extracting(UserHistoryItem::getUsername)
                .containsExactly("Rodion");

        assertThat(historyUser1)
                .extracting(UserHistoryItem::getTextQuestion)
                .containsExactly("What is JVM?", "What is DI?");

        assertThat(historyUser2)
                .extracting(UserHistoryItem::getTextQuestion)
                .containsExactly("What is Spring?");

        assertThat(historyUser2)
                .extracting(UserHistoryItem::getAnswerText)
                .containsExactly("JVM executes bytecode.");

        assertThat(historyUser1.get(0).getAnswerText())
                .isEqualTo("JVM executes bytecode.");

        assertThat(historyUser1.get(0).getModelName())
                .isEqualTo("mock-ai");

        assertThat(historyUser1.get(1).getAnswerText()).isNull();
        assertThat(historyUser1.get(1).getModelName()).isNull();

    }
}
