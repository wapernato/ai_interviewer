package org.example.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.response.auth.AuthResponse;
import org.example.model.AiProfile;
import org.example.model.Question;
import org.example.model.QuestionDifficulty;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.AiProfileRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.TopicRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthRestAssuredTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AiProfileRepository aiProfileRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @BeforeEach
    void setUp(){
        questionRepository.deleteAll();
        topicRepository.deleteAll();
        aiProfileRepository.deleteAll();
        userRepository.deleteAll();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private RegisterRequest createRegisterRequest(String username, String email, String password){
        RegisterRequest request = new RegisterRequest();

        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    private LoginRequest createLoginRequest(String email, String password){
        LoginRequest request = new LoginRequest();

        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    private AiProfile createActiveAiProfile() {
        AiProfile aiProfile = new AiProfile();

        aiProfile.setMode("e2e-" + UUID.randomUUID());
        aiProfile.setDescriptionMode("E2E test profile");
        aiProfile.setInstructionMode("Generate interview questions for E2E tests");
        aiProfile.setModelName("test-model");
        aiProfile.setLanguage("ru");
        aiProfile.setAnswerStyle("detailed");
        aiProfile.setDifficulty("medium");
        aiProfile.setFeedbackMode("detailed");
        aiProfile.setHintMode(false);
        aiProfile.setActive(true);
        aiProfile.setTemperature(0.7);
        aiProfile.setMaxTokens(1000);

        return aiProfileRepository.saveAndFlush(aiProfile);
    }

    @Test
    void register_shouldCreateUser_whenRequestIsValid(){
        RegisterRequest request = createRegisterRequest(
                "ximeo",
                "ximeo@gmail.com",
                "88888888");

        Response rawResponse =
                given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("username", equalTo("ximeo"))
                .body("email", equalTo("ximeo@gmail.com"))
                .body("role", equalTo("USER"))
                .body("token", nullValue())
                .body("$", not(hasKey("password")))
                .body("$", not(hasKey("passwordHash")))
                .extract()
                .response();

        AuthResponse authResponse = rawResponse.as(AuthResponse.class);
        Long responseId = authResponse.getId();

        User savedUser = userRepository.findByEmail("ximeo@gmail.com")
                .orElseThrow();

        assertThat(responseId)
                .isNotNull()
                .isPositive()
                .isEqualTo(savedUser.getId());

        assertThat(savedUser.getUsername()).isEqualTo("ximeo");
        assertThat(savedUser.getEmail()).isEqualTo("ximeo@gmail.com");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.getPasswordHash()).isNotNull();
        assertThat(savedUser.getPasswordHash()).isNotBlank();
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("88888888");
        assertThat(passwordEncoder.matches(
                "88888888",
                savedUser.getPasswordHash()
        )).isTrue();
    }

    @Test
    void question_shouldUseAuthenticatedUser_whenBodyContainsAnotherUserId() {
        AiProfile aiProfile = createActiveAiProfile();

        RegisterRequest requestUserXimeo = createRegisterRequest(
                "ximeo",
                "ximeo@gmail.com",
                "12345678");

        RegisterRequest requestUserRodion = createRegisterRequest(
                "rodion",
                "rodion@gmail.com",
                "12345678");

        Response responseRegisterXimeo = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestUserXimeo)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("username", equalTo("ximeo"))
                .extract()
                .response();

        Response responseRegisterRodion = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestUserRodion)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("username", equalTo("rodion"))
                .extract()
                .response();

        LoginRequest loginRequestRodion = createLoginRequest("rodion@gmail.com", "12345678");

        Response responseLoginRodion =
                given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(loginRequestRodion)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        AuthResponse responseXimeo = responseRegisterXimeo.as(AuthResponse.class);
        AuthResponse responseRodionRegistration = responseRegisterRodion.as(AuthResponse.class);
        AuthResponse responseRodion = responseLoginRodion.as(AuthResponse.class);

        Long ximeoId = responseXimeo.getId();
        Long rodionId = responseRodionRegistration.getId();
        String jwtRodion = responseRodion.getToken();

        Map<String, Object> maliciousRequest = Map.of(
                "topic", "Java",
                "aiProfileId", aiProfile.getId(),
                "userId", ximeoId
        );

        Response questionResponse = given()
                .auth()
                .oauth2(jwtRodion)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(maliciousRequest)
                .when()
                .post("/api/interview/question")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .extract()
                .response();

        InterviewQuestionResult result = questionResponse.as(InterviewQuestionResult.class);

        assertThat(result.getQuestionId()).isNotNull().isPositive();
        assertThat(result.getUserId()).isEqualTo(rodionId).isNotEqualTo(ximeoId);
        assertThat(result.getTopicId()).isNotNull().isPositive();
        assertThat(result.getTopicName()).isEqualTo("Java");
        assertThat(result.getQuestionText()).isEqualTo("Временный вопрос по теме: Java");
        assertThat(result.getAiProfileId()).isEqualTo(aiProfile.getId());
        assertThat(result.getAiMode()).isEqualTo(aiProfile.getMode());
        assertThat(result.getDifficulty()).isEqualTo(QuestionDifficulty.HARD);

        Question savedQuestion = questionRepository.findById(result.getQuestionId())
                .orElseThrow();

        assertThat(savedQuestion.getTextQuestion()).isEqualTo(result.getQuestionText());
        assertThat(savedQuestion.getDifficulty()).isEqualTo(QuestionDifficulty.HARD);
        assertThat(questionRepository.findByCreatedByUser_Id(rodionId))
                .extracting(Question::getId)
                .containsExactly(result.getQuestionId());
        assertThat(questionRepository.findByCreatedByUser_Id(ximeoId)).isEmpty();
        assertThat(topicRepository.findById(result.getTopicId()))
                .get()
                .extracting("name")
                .isEqualTo("Java");

    }

    @Test
    void question_shouldReturnBadRequest_whenTopicHasWrongType() {

    }
}
