package tests.e2e

import config.ApiConfig
import dto.auth.LoginRequest
import dto.auth.RegisterRequest
import dto.interview.AnswerRequest
import dto.interview.QuestionRequest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.hamcrest.Matchers.equalTo
import kotlin.test.Test

class InterviewFlowApiTest {

    @Test
    fun `answer should return bad request when user A answers user B question`() {
        val uniqueSuffix = System.currentTimeMillis()

        val userAUsername = "user_a_$uniqueSuffix"
        val userAEmail = "user_a_$uniqueSuffix@gmail.com"
        val userAPassword = "12345678"

        val userARegisterRequest = RegisterRequest(
            username = userAUsername,
            email = userAEmail,
            password = userAPassword
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(userARegisterRequest)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)

        val userALoginRequest = LoginRequest(
            email = userAEmail,
            password = userAPassword
        )

        val userALoginResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(userALoginRequest)
        .`when`()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response()

        val userAUserToken = userALoginResponse.jsonPath().getString("token")

        val userBUsername = "user_b_$uniqueSuffix"
        val userBEmail = "user_b_$uniqueSuffix@gmail.com"
        val userBPassword = "12345678"

        val userBRegisterRequest = RegisterRequest(
            username = userBUsername,
            email = userBEmail,
            password = userBPassword
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(userBRegisterRequest)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)

        val userBLoginRequest = LoginRequest(
            email = userBEmail,
            password = userBPassword
        )

        val userBLoginResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(userBLoginRequest)
        .`when`()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response()

        val userBId = userBLoginResponse.jsonPath().getLong("id")
        val userBToken = userBLoginResponse.jsonPath().getString("token")

        val questionRequest = QuestionRequest(
            topic = "Java"
        )

        val questionResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .auth()
            .oauth2(userBToken)
            .body(questionRequest)
        .`when`()
            .post("/api/interview/question")
        .then()
            .statusCode(201)
            .extract()
            .response()

        val userBQuestionId = questionResponse.jsonPath().getLong("questionId")

        val answerRequest = AnswerRequest(
            userId = userBId,
            questionId = userBQuestionId,
            textAnswer = "Раз, 2, три, 4, пять!"
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .auth()
            .oauth2(userAUserToken)
            .body(answerRequest)
        .`when`()
            .post("/api/interview/answer")
        .then()
            .statusCode(400)
            .contentType(JSON)
            .body("error", equalTo("BAD_REQUEST"))
            .body("message", equalTo("Нельзя ответить на вопрос другого пользователя."))
    }

    @Test
    fun `answer should return ok when data is valid`() {
        val uniqueSuffix = System.currentTimeMillis()

        val username = "user$uniqueSuffix"
        val email = "user$uniqueSuffix@gmail.com"
        val password = "12345678"

        val body = RegisterRequest(
            username = username,
            email = email,
            password = password
        )

        val registerResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(body)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .response()

        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        val loginResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(loginRequest)
        .`when`()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response()

        val questionRequest = QuestionRequest(
            topic = "Алгоритмы"
        )

        val jwt = loginResponse.jsonPath().getString("token")

        val questionResponse = given()
            .auth().oauth2(jwt)
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(questionRequest)
        .`when`()
            .post("/api/interview/question")
        .then()
            .statusCode(201)
            .extract()
            .response()

        val answerRequest = AnswerRequest(
            userId = loginResponse.jsonPath().getLong("id"),
            questionId = questionResponse.jsonPath().getLong("questionId"),
            textAnswer = "Бинарное дерево."
        )

        given()
            .auth().oauth2(jwt)
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(answerRequest)
        .`when`()
            .post("/api/interview/answer")
        .then()
            .statusCode(201)
    }
}
