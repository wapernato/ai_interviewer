package tests.auth

import config.ApiConfig
import dto.auth.RegisterRequest
import org.junit.jupiter.api.Test
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue

class AuthRegisterApiTest {

    @Test
    fun register_shouldCreateUser_whenRequestIsValid(){
        val uniqueSuffix = System.currentTimeMillis()

        val username = "ximeo_$uniqueSuffix"
        val email = "ximeo_$uniqueSuffix@gmail.com"
        val password = "12345678"

        val body = RegisterRequest(
            username = username,
            email = email,
            password = password
        )

        val response = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(body)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .contentType(JSON)
            .body("$", not(hasKey("password")))
            .body("$", not(hasKey("passwordHash")))
            .body("id", notNullValue())
            .body("username", equalTo(username))
            .body("email", equalTo(email))
            .body("role", equalTo("USER"))
            .extract()
            .response()

        val userId = response.jsonPath().getLong("id")

        given()
            .baseUri(ApiConfig.baseUrl)
        .`when`()
            .delete("/api/users/$userId")
        .then()
            .statusCode(204)
    }

    @Test
    fun register_shouldReturnConflict_whenEmailAlreadyExists(){
        val uniqueSuffix = System.currentTimeMillis()

        val ximeoUsername = "ximeo_$uniqueSuffix"
        val ximeoEmail = "ximeo_$uniqueSuffix@gmail.com"
        val ximeoPassword = "12345678"

        val ximeoBody = RegisterRequest(
            username = ximeoUsername,
            email = ximeoEmail,
            password = ximeoPassword
        )

        val rodionUsername = "rodion$uniqueSuffix"
        val rodionPassword = "12345678"

        val rodionBody = RegisterRequest(
            username = rodionUsername,
            email = ximeoEmail,
            password = rodionPassword
        )

        val ximeoResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(ximeoBody)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .response()

        val rodionResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(rodionBody)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(409)
            .contentType(JSON)
            .body("status", equalTo(409))
            .body("error", equalTo("CONFLICT"))
            .body("message", equalTo("Пользователь с таким email уже существует."))
            .extract()
            .response()

        val ximeoId = ximeoResponse.jsonPath().getLong("id")

        given()
            .baseUri(ApiConfig.baseUrl)
        .`when`()
            .delete("/api/users/$ximeoId")
        .then()
            .statusCode(204)
    }

    @Test
    fun register_shouldReturnBadRequest_whenEmailIsBlank(){
        val uniqueSuffix = System.currentTimeMillis()

        val username = "ximeo_$uniqueSuffix"
        val email = ""
        val password = "12345678"

        val body = RegisterRequest(
            username = username,
            email = email,
            password = password
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(body)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(400)
            .body("error", equalTo("BAD_REQUEST"))
            .body("message", equalTo("Ошибка валидации данных."))
            .body("validationErrors.email", equalTo("Email не должен быть пустым."))
    }


}
