package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.exception.NotFoundException;
import org.example.model.EmailVerificationToken;
import org.example.model.User;
import org.example.repository.EmailVerificationTokenRepository;
import org.example.repository.UserRepository;
import org.example.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmailVerificationIntegrationTest {

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
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @MockitoBean
    private EmailSenderService emailSenderService;

    @BeforeEach
    void setUp() {
        emailVerificationTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void confirmEmail_shouldEnableUserAndUseToken_whenTokenIsValid() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setEmail("ximeo@gmail.com");
        request.setUsername("ximeo");
        request.setPassword("3creu!@vdFE67");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<EmailVerificationToken> verificationTokens = emailVerificationTokenRepository.findAll();

        assertThat(verificationTokens).hasSize(1);

        String token = verificationTokens.get(0).getToken();

        mockMvc.perform(get("/api/auth/confirm").param("token", token))
                .andExpect(status().isNoContent());

        User user = userRepository.findByEmail("ximeo@gmail.com")
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));


        EmailVerificationToken verificationTokensActivate = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Токен не найден."));

        assertThat(user.getEnabled()).isTrue();
        assertThat(verificationTokensActivate.getUsedAt()).isNotNull();

    }

    @Test
    void login_shouldReturnBadRequest_whenEmailIsNotConfirmed() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("rodion@gmail.com");
        registerRequest.setUsername("Rodion");
        registerRequest.setPassword("3creu!@vdFE67");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("rodion@gmail.com");
        loginRequest.setPassword("3creu!@vdFE67");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email пользователя не подтвержден."));
    }

    @Test
    @Tag("regression")
    void login_shouldLoginUser_whenEmailIsConfirmed() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("rodion@gmail.com");
        registerRequest.setUsername("Rodion");
        registerRequest.setPassword("3creu!@vdFE67");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(registerRequest)))
                .andExpect(status().isCreated());

        List<EmailVerificationToken> verificationTokens = emailVerificationTokenRepository.findAll();

        String token = verificationTokens.get(0).getToken();

        mockMvc.perform(get("/api/auth/confirm").param("token", token))
                .andExpect(status().isNoContent());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("rodion@gmail.com");
        loginRequest.setPassword("3creu!@vdFE67");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("rodion@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void confirmEmail_shouldReturnBadRequest_whenTokenIsAlreadyUsed() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("rodion@gmail.com");
        registerRequest.setUsername("wapernato67");
        registerRequest.setPassword("3creu!@vdFE67");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        List<EmailVerificationToken> verificationTokens = emailVerificationTokenRepository.findAll();

        String token = verificationTokens.get(0).getToken();

        mockMvc.perform(get("/api/auth/confirm").param("token", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/auth/confirm").param("token", token))
                .andExpect(status().isBadRequest());
    }

}
