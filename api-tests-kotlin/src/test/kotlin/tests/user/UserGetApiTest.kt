package tests.user

import config.ApiConfig
import dto.auth.LoginRequest
import dto.auth.RegisterRequest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.not
import kotlin.test.Test

class UserGetApiTest {

    @Test
    fun `get me should return current user when token is valid`(){
        val uniqueSuffix = System.currentTimeMillis()

        val username = "ximeo_$uniqueSuffix"
        val email = "ximeo_$uniqueSuffix@gmail.com"
        val password = "12345678"

        val registerBody = RegisterRequest(
            username = username,
            email = email,
            password = password
        )

        val response = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(registerBody)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .response()

        val userId = response.jsonPath().getLong("id")

        val loginBody = LoginRequest(
            email = email,
            password = password
        )

        val loginResponse = given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(loginBody)
        .`when`()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .extract()
            .response()

        val token = loginResponse.jsonPath().getString("token")

        given()
            .baseUri(ApiConfig.baseUrl)
            .accept(JSON)
            .auth().oauth2(token)
        .`when`()
            .get("/api/me")
        .then()
            .statusCode(200)
            .body("username", equalTo(username))
            .body("email", equalTo(email))
            .body("id", equalTo(userId.toInt()))
            .body("role", equalTo("USER"))
            .body("$", not(hasKey("password")))
            .body("$", not(hasKey("passwordHash")))
    }
}
