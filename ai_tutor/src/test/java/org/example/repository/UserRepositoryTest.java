package org.example.repository;

import org.example.model.User;
import org.example.model.UserRole;
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
class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username, String email) {
        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("encoded-password");
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        return user;
    }

    @Test
    void save_shouldThrowException_whenUsernameIsDuplicate() {
        User firstUser = createUser("Yakov", "dygv@gmail.com");
        User secondUser = createUser("Yakov", "kmsd@gmail.com");

        userRepository.saveAndFlush(firstUser);

        assertThatThrownBy(() -> userRepository.saveAndFlush(secondUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_shouldThrowException_whenEmailIsDuplicate() {
        User firstUser = createUser("Rodion", "kms@gmail.com");
        User secondUser = createUser("Yakov", "kms@gmail.com");

        userRepository.saveAndFlush(firstUser);

        assertThatThrownBy(() -> userRepository.saveAndFlush(secondUser))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_shouldPersistUser_whenDataIsValid() {
        User user = createUser("Yakov", "kms@yandex.ru");

        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("Yakov");
        assertThat(savedUser.getEmail()).isEqualTo("kms@yandex.ru");
    }


}
