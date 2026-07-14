package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.model.UserRole;
import org.example.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private RegisterRequest createRegisterRequest(String username, String email, String password){
        RegisterRequest request = new RegisterRequest();

        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }
    @Test
    void register_shouldReturnCreated_whenRequestIsValid() throws Exception {
        RegisterRequest request = createRegisterRequest("ximeo", "zavod3433@yandex.ru", "88888888");

        AuthResponse response = new AuthResponse();
        response.setId(1L);
        response.setUsername("ximeo");
        response.setEmail("zavod3433@yandex.ru");
        response.setRole(UserRole.USER);

        when(authService.register(request)).thenReturn(response);

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
}







