package org.example.repository;

import org.example.model.AiProfile;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AiProfileRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private AiProfileRepository aiProfileRepository;

    private AiProfile createAiProfile(String mode, boolean active) {
        AiProfile aiProfile = new AiProfile();

        aiProfile.setMode(mode);
        aiProfile.setDescriptionMode(mode + " description");
        aiProfile.setInstructionMode("Instruction for " + mode);
        aiProfile.setModelName("mock-ai");
        aiProfile.setLanguage("ru");
        aiProfile.setAnswerStyle("detailed");
        aiProfile.setDifficulty("medium");
        aiProfile.setFeedbackMode("detailed");
        aiProfile.setHintMode(false);
        aiProfile.setActive(active);
        aiProfile.setTemperature(0.7);
        aiProfile.setMaxTokens(1000);

        return aiProfile;
    }

    @Test
    void save_shouldThrowException_whenSecondActiveProfileIsSaved() {
        aiProfileRepository.findAll().forEach(profile -> profile.setActive(false));
        aiProfileRepository.flush();

        AiProfile aiProfile1 = createAiProfile("First", true);
        AiProfile aiProfile2 = createAiProfile("Second", true);

        aiProfileRepository.saveAndFlush(aiProfile1);


        assertThatThrownBy(() -> aiProfileRepository.saveAndFlush(aiProfile2))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void save_shouldAllowManyInactiveProfiles() {
        aiProfileRepository.findAll().forEach(profile -> profile.setActive(false));
        aiProfileRepository.flush();

        AiProfile aiProfile1 = createAiProfile("First", false);
        AiProfile aiProfile2 = createAiProfile("Second", false);

        AiProfile saveAndFlush1 = aiProfileRepository.saveAndFlush(aiProfile1);
        AiProfile saveAndFlush2 = aiProfileRepository.saveAndFlush(aiProfile2);

        assertThat(saveAndFlush1.getId()).isNotNull();
        assertThat(saveAndFlush2.getId()).isNotNull();
    }

    @Test
    void save_shouldThrowException_whenModeIsDuplicate() {
        AiProfile aiProfile1 = createAiProfile("interview", false);
        AiProfile aiProfile2 = createAiProfile("interview", false);

        aiProfileRepository.saveAndFlush(aiProfile1);

        assertThatThrownBy(() -> aiProfileRepository.saveAndFlush(aiProfile2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
