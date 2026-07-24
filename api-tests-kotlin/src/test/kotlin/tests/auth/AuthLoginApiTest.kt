package tests.auth

import config.ApiConfig
import dto.auth.LoginRequest
import dto.auth.RegisterRequest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.hamcrest.Matchers.equalTo
import kotlin.test.Test

class AuthLoginApiTest {

    @Test
    fun `login shouldLoginUser whenDataIsValid`(){
        val uniqueSuffix = System.currentTimeMillis()

        val username = "ximeo_$uniqueSuffix"
        val email = "ximeo_$uniqueSuffix@gmail.com"
        val password = "12345678"

        val registerBody = RegisterRequest(
            username = username,
            email = email,
            password = password
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(registerBody)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)

        val loginBody = LoginRequest(
            email = email,
            password = password
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(loginBody)
        .`when`()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
    }

    @Test
    fun `login shouldBadRequest whenPasswordIsInvalid`(){
        val uniqueSuffix = System.currentTimeMillis()

        val username = "ximeo_$uniqueSuffix"
        val email = "ximeo_$uniqueSuffix@gmail.com"
        val password = "12345678"

        val registerBody = RegisterRequest(
            username = username,
            email = email,
            password = password
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(registerBody)
        .`when`()
            .post("/api/auth/register")
        .then()
            .statusCode(201)


        val loginBody = LoginRequest(
            email = email,
            password = "87654321"
        )

        given()
            .baseUri(ApiConfig.baseUrl)
            .contentType(JSON)
            .accept(JSON)
            .body(loginBody)
        .`when`()
            .post("/api/auth/login")
        .then()
            .statusCode(400)
            .body("error", equalTo("BAD_REQUEST"))
            .body("message", equalTo("Неверный email или пароль."))

    }
}