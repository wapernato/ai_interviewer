package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class AuthRegisterIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    private RegisterRequest createRegister(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();

        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    @Test
    void register_shouldSaveUser_whenDataIsValid() throws Exception {
        RegisterRequest request = createRegister("ximeo", "ximeo@yandex.ru", "88888888");

        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);

        assertThat(authResponse.getUsername()).isEqualTo("ximeo");
        assertThat(authResponse.getEmail()).isEqualTo("ximeo@yandex.ru");
        assertThat(authResponse.getRole()).isEqualTo(UserRole.USER);

        Optional<User> savedUser = userRepository.findByEmail("ximeo@yandex.ru");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo("ximeo");
        assertThat(savedUser.get().getEmail()).isEqualTo("ximeo@yandex.ru");
        assertThat(savedUser.get().getPasswordHash()).isNotEqualTo("88888888");
        assertThat(passwordEncoder.matches("88888888", savedUser.get().getPasswordHash())).isTrue();
    }

    @Test
    void register_shouldReturnConflictAndNotSaveUser_whenEmailAlreadyExists() throws Exception {
        RegisterRequest firstRequest = createRegister("ximeo", "ximeo@yandex.ru", "88888888");
        RegisterRequest secondRequest = createRegister("waper", "ximeo@yandex.ru", "88888888");

        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);

        assertThat(authResponse.getEmail()).isEqualTo("ximeo@yandex.ru");
        assertThat(authResponse.getUsername()).isEqualTo("ximeo");
        assertThat(authResponse.getRole()).isEqualTo(UserRole.USER);

        Optional<User> savedUser = userRepository.findByEmail("ximeo@yandex.ru");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo("ximeo");
        assertThat(savedUser.get().getEmail()).isEqualTo("ximeo@yandex.ru");
        assertThat(savedUser.get().getPasswordHash()).isNotEqualTo("88888888");
        assertThat(passwordEncoder.matches("88888888", savedUser.get().getPasswordHash())).isTrue();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Пользователь с таким email уже существует."));

        Optional<User> notSavedUser = userRepository.findByUsername("waper");
        assertThat(notSavedUser).isEmpty();
    }

    @Test
    void register_shouldIgnoreRoleFromRequest_whenRoleIsProvided() throws Exception {
        String body = """
                {
                  "username": "ximeo",
                  "email": "ximeo@gmail.com",
                  "password": "88888888",
                  "role": "ADMIN"
                }
                """;

        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);

        assertThat(authResponse.getEmail()).isEqualTo("ximeo@gmail.com");
        assertThat(authResponse.getUsername()).isEqualTo("ximeo");
        assertThat(authResponse.getRole()).isEqualTo(UserRole.USER);

        Optional<User> savedUser = userRepository.findByEmail("ximeo@gmail.com");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getRole()).isEqualTo(UserRole.USER);
    }
}
