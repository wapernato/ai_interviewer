package org.example.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.interview.AnswerRequest;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.interview.QuestionRequest;
import org.example.dto.response.auth.AuthResponse;
import org.example.model.AiProfile;
import org.example.repository.AiProfileRepository;
import org.example.repository.QuestionRepository;
import org.example.repository.TopicRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import io.restassured.response.Response;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class InterviewEndTest {
    private static final String VALID_PASSWORD = "StrongPass1!";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AiProfileRepository aiProfileRepository;

    @Autowired
    private UserRepository userRepository;

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


    @LocalServerPort
    private int port;

    @Test
    void interview_shouldCompleteSuccessfully_whenAuthenticatedUserAnswersQuestion() {
        AiProfile aiProfile = createActiveAiProfile();

        RegisterRequest request = createRegisterRequest("ximeo", "ximeo@gmail.com", VALID_PASSWORD);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201);

        LoginRequest loginRequest = createLoginRequest("ximeo@gmail.com", VALID_PASSWORD);

        Response responseLogin = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        QuestionRequest questionRequest = new QuestionRequest("Java");
        questionRequest.setAiProfileId(aiProfile.getId());

        AuthResponse authResponse = responseLogin.as(AuthResponse.class);
        String jwt = authResponse.getToken();

        Response responseQuestion = given()
                .auth()
                .oauth2(jwt)
                .contentType(ContentType.JSON)
                .body(questionRequest)
                .when()
                .post("/api/interview/question")
                .then()
                .statusCode(201)
                .extract()
                .response();

        InterviewQuestionResult questionResult = responseQuestion.as(InterviewQuestionResult.class);

        AnswerRequest answerRequest = new AnswerRequest();

        answerRequest.setQuestionId(questionResult.getQuestionId());
        answerRequest.setTextAnswer("JVM исполняет Java-байткод и управляет памятью.");

        Response response =
                given()
                .auth()
                .oauth2(jwt)
                .contentType(ContentType.JSON)
                .body(answerRequest)
                .when()
                .post("/api/interview/answer")
                .then()
                .statusCode(201)
                .extract()
                .response();

        InterviewAnswerResult answerResult = response.as(InterviewAnswerResult.class);

        assertThat(jwt).isNotBlank();

        assertThat(questionResult.getQuestionId())
                .isNotNull()
                .isPositive();

        assertThat(questionResult.getUserId())
                .isEqualTo(authResponse.getId());

        assertThat(questionResult.getTopicName())
                .isEqualTo("Java");

        assertThat(answerResult.getAnswerId())
                .isNotNull()
                .isPositive();

        assertThat(answerResult.getUserId())
                .isEqualTo(authResponse.getId());

        assertThat(answerResult.getQuestionId())
                .isEqualTo(questionResult.getQuestionId());

        assertThat(answerResult.getQuestionText())
                .isEqualTo(questionResult.getQuestionText());

        assertThat(answerResult.getUserAnswerText())
                .isEqualTo("JVM исполняет Java-байткод и управляет памятью.");

        assertThat(answerResult.getFeedback())
                .isNotBlank();
    }

}
