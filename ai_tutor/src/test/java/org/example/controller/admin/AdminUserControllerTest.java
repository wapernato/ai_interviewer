package org.example.controller.admin;

import org.example.dto.response.user.UserResponse;
import org.example.exception.NotFoundException;
import org.example.service.AdminUserService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AdminUserService adminUserService;

    private UserResponse createUserResponse(Long id, String username) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setUsername(username);
        return response;
    }

    @Test
    void getAllUsers_shouldReturnUsers_whenUsersExist() throws Exception {
        List<UserResponse> users = List.of(
                createUserResponse(1L, "Yakov"),
                createUserResponse(2L, "Rodion")
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("Yakov"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("Rodion"));

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenUsersDoNotExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenDataIsValid() throws Exception {
        UserResponse response = createUserResponse(1L, "Yakov");

        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));

        verify(userService).getById(1L);
    }

    @Test
    void getUserById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/admin/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userService.getById(1L))
                .thenThrow(new NotFoundException("Пользователь с id=1 не найден."));

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(userService).getById(1L);
    }

    @Test
    void deleteUserById_shouldReturnNoContent_whenUserExists() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService).deleteById(1L);
    }

    @Test
    void deleteUserById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(delete("/api/admin/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void deleteUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Пользователь с id=1 не найден."))
                .when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(userService).deleteById(1L);
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() throws Exception {
        UserResponse response = createUserResponse(1L, "Yakov");

        when(userService.findByName("Yakov")).thenReturn(response);

        mockMvc.perform(get("/api/admin/users/search")
                        .param("username", "Yakov"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));

        verify(userService).findByName("Yakov");
    }

    @Test
    void findByUsername_shouldReturnBadRequest_whenUsernameIsMissing() throws Exception {
        mockMvc.perform(get("/api/admin/users/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Отсутствует обязательный параметр запроса: username"));

        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception {
        mockMvc.perform(get("/api/admin/users/search")
                        .param("username", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userService.findByName("Unknown"))
                .thenThrow(new NotFoundException("Пользователь с именем (Unknown) не найден."));

        mockMvc.perform(get("/api/admin/users/search")
                        .param("username", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с именем (Unknown) не найден."));

        verify(userService).findByName("Unknown");
    }
}
