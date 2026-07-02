package org.example.repository;

import org.example.model.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TopicRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void findByName_shouldReturnTopic_whenDataIsValid() {
        Topic topic = new Topic("Java");
        topicRepository.save(topic);

        Optional<Topic> optional = topicRepository.findByName("Java");

        assertThat(optional).isPresent();
        assertThat(optional.get().getId()).isNotNull();
        assertThat(optional.get().getName()).isEqualTo("Java");
    }

    @Test
    void findByName_shouldReturnEmpty_whenTopicDoesNotExist() {
        Optional<Topic> optional = topicRepository.findByName("Unknown");

        assertThat(optional).isEmpty();
    }

    @Test
    void existsByName_shouldReturnTrue_whenTopicExist() {
        Topic topic = new Topic("Java");
        topicRepository.save(topic);

        boolean exists = topicRepository.existsByName("Java");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_shouldReturnFalse_whenTopicDoesNotExist() {
        boolean exists = topicRepository.existsByName("Java");

        assertThat(exists).isFalse();
    }

    @Test
    void save_shouldThrowException_whenNameIsDuplicate() {
        Topic firstTopic = new Topic("Java");
        Topic secondTopic = new Topic("Java");

        topicRepository.saveAndFlush(firstTopic);

        assertThatThrownBy(() -> topicRepository.saveAndFlush(secondTopic))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
