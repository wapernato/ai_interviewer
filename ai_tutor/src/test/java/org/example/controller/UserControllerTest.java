package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.response.UserResponse;
import org.example.dto.user.UpdateUserRequest;
import org.example.exception.NotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.security.JwtService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private UserResponse createUserResponse(Long id, String username) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setUsername(username);
        return response;
    }

    @Test
    void getMe_shouldReturnCurrentUser_whenUserExists() throws Exception {
        UserResponse response = createUserResponse(1L, "Yakov");

        when(jwtService.extractUserId(any())).thenReturn(1L);
        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));

        verify(jwtService).extractUserId(any());
        verify(userService).getById(1L);
    }

    @Test
    void getMe_shouldReturnNotFound_whenCurrentUserDoesNotExist() throws Exception {
        when(jwtService.extractUserId(any())).thenReturn(1L);
        when(userService.getById(1L))
                .thenThrow(new NotFoundException("Пользователь с id=1 не найден."));

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(jwtService).extractUserId(any());
        verify(userService).getById(1L);
    }

    @Test
    void updateMe_shouldReturnUserResponse_whenDataIsValid() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Yakov");
        UserResponse response = createUserResponse(1L, "Yakov");

        when(jwtService.extractUserId(any())).thenReturn(1L);
        when(userService.updateUsername(1L, "Yakov")).thenReturn(response);

        mockMvc.perform(put("/api/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));

        verify(jwtService).extractUserId(any());
        verify(userService).updateUsername(1L, "Yakov");
    }

    @Test
    void updateMe_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("  ");

        mockMvc.perform(put("/api/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных."))
                .andExpect(jsonPath("$.validationErrors.username").value("Имя пользователя не может быть пустым."));

        verifyNoInteractions(userService);
    }

    @Test
    void updateMe_shouldReturnNotFound_whenCurrentUserDoesNotExist() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Rodion");

        when(jwtService.extractUserId(any())).thenReturn(1L);
        when(userService.updateUsername(1L, "Rodion"))
                .thenThrow(new NotFoundException("Пользователь с id=1 не найден."));

        mockMvc.perform(put("/api/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(jwtService).extractUserId(any());
        verify(userService).updateUsername(1L, "Rodion");
    }

    @Test
    void updateMe_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Rodion");

        when(jwtService.extractUserId(any())).thenReturn(1L);
        when(userService.updateUsername(1L, "Rodion"))
                .thenThrow(new UserAlreadyExistsException("Пользователь с таким именем уже существует."));

        mockMvc.perform(put("/api/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Пользователь с таким именем уже существует."));

        verify(jwtService).extractUserId(any());
        verify(userService).updateUsername(1L, "Rodion");
    }

    @Test
    void deleteMe_shouldReturnNoContent_whenCurrentUserExists() throws Exception {
        when(jwtService.extractUserId(any())).thenReturn(1L);

        mockMvc.perform(delete("/api/me"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(jwtService).extractUserId(any());
        verify(userService).deleteById(1L);
    }

    @Test
    void deleteMe_shouldReturnNotFound_whenCurrentUserDoesNotExist() throws Exception {
        when(jwtService.extractUserId(any())).thenReturn(1L);
        doThrow(new NotFoundException("Пользователь с id=1 не найден."))
                .when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/me"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(jwtService).extractUserId(any());
        verify(userService).deleteById(1L);
    }
}
