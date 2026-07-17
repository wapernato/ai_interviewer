package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.exception.BadRequestException;
import org.example.exception.UserAlreadyExistsException;
import org.example.model.UserRole;
import org.example.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private RegisterRequest createRegisterRequest(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();

        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    private AuthResponse createAuthResponse(Long id, String username, String email, UserRole role) {
        AuthResponse response = new AuthResponse();

        response.setId(id);
        response.setUsername(username);
        response.setEmail(email);
        response.setRole(role);

        return response;
    }
    @Test
    void register_shouldReturnCreated_whenRequestIsValid() throws Exception {
        RegisterRequest request = createRegisterRequest("ximeo", "zavod3433@yandex.ru", "88888888");
        AuthResponse response = createAuthResponse(1L, "ximeo", "zavod3433@yandex.ru", UserRole.USER);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("ximeo"))
                .andExpect(jsonPath("$.email").value("zavod3433@yandex.ru"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.token").doesNotExist());

        verify(authService).register(argThat(req ->
                "ximeo".equals(req.getUsername())
                        && "zavod3433@yandex.ru".equals(req.getEmail())
                        && "88888888".equals(req.getPassword())
        ));
    }

    @Test
     void register_shouldThrowBarRequestException_whenNameIsNull() throws Exception {
        RegisterRequest request = createRegisterRequest(null, "zavod3433@yandex.ru", "88888888");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных."))
                .andExpect(jsonPath("$.validationErrors.username").value("Имя пользователя не должно быть пустым."));

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception {
        RegisterRequest request = createRegisterRequest("ximeo","zavod3433@yandex.ru", "88888888" );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Пользователь с таким username уже существует."));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message")
                        .value("Пользователь с таким username уже существует."));

        verify(authService).register(argThat(req ->
                "ximeo".equals(req.getUsername())
                        && "zavod3433@yandex.ru".equals(req.getEmail())
                        && "88888888".equals(req.getPassword())
                ));
    }

    @Test
    void register_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{username:}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturnUnsupportedMediaType_whenContentTypeIsTextPlain() throws Exception {
        String body = """
                        {
                          "username": "ximeo",
                          "email": "zavod3433@yandex.ru",
                          "password": "88888888"
                        }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.TEXT_PLAIN)
                .content(body))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturnBadRequest_whenContentTypeIsJsonButBodyIsPlainText() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("hello"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldIgnoreUnknownJsonFields_whenRoleIsProvided() throws Exception {
        AuthResponse response = createAuthResponse(1L, "ximeo", "zavod3433@yandex.ru", UserRole.USER);
        String body = """
                {
                  "username": "ximeo",
                  "email": "zavod3433@yandex.ru",
                  "password": "88888888",
                  "role": "ADMIN"
                }
                """;

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).register(argThat(req ->
                "ximeo".equals(req.getUsername())
                        && "zavod3433@yandex.ru".equals(req.getEmail())
                        && "88888888".equals(req.getPassword())
        ));
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestBodyIsEmptyJsonObject() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.username").value("Имя пользователя не должно быть пустым."))
                .andExpect(jsonPath("$.validationErrors.email").value("Email не должен быть пустым."))
                .andExpect(jsonPath("$.validationErrors.password").value("Пароль не должен быть пустым."));

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturnBadRequestWithMultipleValidationErrors_whenSeveralFieldsAreInvalid() throws Exception {
        RegisterRequest request = createRegisterRequest("x", "bad-email", "123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.validationErrors.username").value("Имя пользователя должно быть от 2 до 50 символов."))
                .andExpect(jsonPath("$.validationErrors.email").value("Email должен быть корректным."))
                .andExpect(jsonPath("$.validationErrors.password").value("Пароль должен быть от 8 до 100 символов."));

        verifyNoInteractions(authService);
    }
}







