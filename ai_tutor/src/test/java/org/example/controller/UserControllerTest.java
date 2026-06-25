package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.response.UserResponse;
import org.example.dto.user.CreateUserRequest;

import org.example.dto.user.UpdateUserRequest;
import org.example.exception.NotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserResponse createUserResponse(Long id, String username){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(id);
        userResponse.setUsername(username);
        return userResponse;
    }

    @Test
    void getAllUsers_shouldReturnUsers_whenUsersExist() throws Exception {
        List<UserResponse> userResponseList = List.of(
                createUserResponse(1L, "Yakov"),
                createUserResponse(2L, "Rodion")
        );

        when(userService.getAllUsers()).thenReturn(userResponseList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("Yakov"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("Rodion"));

        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenUsersDoNotExist() throws Exception{
        List<UserResponse> userResponseList = List.of();

        when(userService.getAllUsers()).thenReturn(userResponseList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));


        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {

        mockMvc.perform(get("/api/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenDataIsValid() throws Exception{
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("Yakov");

        when(userService.getById(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));

        verify(userService).getById(1L);
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception{

        when(userService.getById(1L))
                .thenThrow(new NotFoundException(
                        "Пользователь с id=1 не найден."
                ));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Пользователь с id=1 не найден."));


        verify(userService).getById(1L);
    }

    @Test
    void createUser_shouldReturnUserResponse_whenDataIsValid() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest("Yakov");

        UserResponse userResponse = createUserResponse(1L, "Yakov");

        when(userService.register("Yakov")).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));


        verify(userService).register("Yakov");
    }

    @Test
    void createUser_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception{

        CreateUserRequest request = new CreateUserRequest("   ");

        mockMvc.perform(post("/api/users")
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
    void createUser_shouldReturnBadRequest_whenUsernameIsNull() throws Exception{

        CreateUserRequest userRequest = new CreateUserRequest(null);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных."))
                .andExpect(jsonPath("$.validationErrors.username").value("Имя пользователя не может быть пустым."));

        verifyNoInteractions(userService);
    }

    @Test
    void createUser_shouldReturnBadRequest_whenUsernameIsTooShort() throws Exception{
        CreateUserRequest userRequest = new CreateUserRequest("a");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных."))
                .andExpect(jsonPath("$.validationErrors.username").value("Длина имени пользователя должна быть от 2 до 50 символов."));

        verifyNoInteractions(userService);
    }

    @Test
    void createUser_shouldReturnBadRequest_whenUsernameIsTooLong() throws Exception{
        CreateUserRequest userRequest = new CreateUserRequest("a".repeat(51));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации данных."))
                .andExpect(jsonPath("$.validationErrors.username").value("Длина имени пользователя должна быть от 2 до 50 символов."));

        verifyNoInteractions(userService);
    }

    @Test
    void createUser_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest("Yakov");

        when(userService.register("Yakov")).thenThrow(new UserAlreadyExistsException(
                "Пользователь с именем 'Yakov' уже существует."
        ));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message")
                        .value("Пользователь с именем 'Yakov' уже существует."));

        verify(userService).register("Yakov");

    }

    @Test
    void createUser_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(userService);
    }

    @Test
    void deleteByUserId_shouldReturnBadRequest_whenIdIsNotValid()
            throws Exception {

                mockMvc.perform(delete("/api/users/0"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value(400))
                        .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

                verifyNoInteractions(userService);
    }

    @Test
    void deleteByUserId_shouldReturnNoContent_whenUserExists() throws Exception {

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService).deleteById(1L);
    }

    @Test
    void deleteByUserId_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        doThrow(new NotFoundException(
                "Пользователь с id=1 не найден."
        )).when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(userService).deleteById(1L);
    }

    @Test
    void updateUserById_shouldReturnUserResponse_whenDataIsValid() throws Exception{
        UpdateUserRequest userRequest = new UpdateUserRequest("Yakov");

        UserResponse userResponse = createUserResponse(1L, "Yakov");

        when(userService.updateUsername(1L, "Yakov")).thenReturn(userResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));


        verify(userService).updateUsername(1L, "Yakov");
    }

    @Test
    void updateUserById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception{

        UpdateUserRequest userRequest = new UpdateUserRequest("Yakov");

        mockMvc.perform(put("/api/users/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров запроса."))
                .andExpect(jsonPath("$.validationErrors.id").value("ID пользователя должен быть положительным числом."));

        verifyNoInteractions(userService);
    }

    @Test
    void updateUserById_shouldReturnBadRequest_whenIdHasWrongType() throws Exception{
        UpdateUserRequest userRequest = new UpdateUserRequest("Yakov");

        mockMvc.perform(put("/api/users/null")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное значение параметра: id"));

        verifyNoInteractions(userService);
    }

    @Test
    void updateUserById_shouldReturnBadRequest_whenUsernameIsNull() throws Exception{
        UpdateUserRequest userRequest = new UpdateUserRequest(null);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров запроса."))
                .andExpect(jsonPath("$.validationErrors.updateUserRequest")
                        .value("Имя пользователя не может быть пустым."));

        verifyNoInteractions(userService);
    }

    @Test
    void updateUserById_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception{
        UpdateUserRequest userRequest = new UpdateUserRequest("  ");

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров запроса."))
                .andExpect(jsonPath("$.validationErrors.updateUserRequest")
                        .value("Имя пользователя не может быть пустым."));

        verifyNoInteractions(userService);

    }

    @Test
    void updateUserById_shouldReturnBadRequest_whenUsernameIsTooShort() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("a");

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.updateUserRequest")
                        .value("Длина имени пользователя должна быть от 2 до 50 символов."));

        verifyNoInteractions(userService);
    }

    @Test
    void updateUserById_shouldReturnBadRequest_whenUsernameIsTooLong() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("a".repeat(51));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.updateUserRequest")
                        .value("Длина имени пользователя должна быть от 2 до 50 символов."));

        verifyNoInteractions(userService);
    }

    @Test
    void updateUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Rodion");
        when(userService.updateUsername(1L, "Rodion"))
                .thenThrow(new NotFoundException("Пользователь с id=1 не найден."));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(userService).updateUsername(1L, "Rodion");
    }

    @Test
    void updateUserById_shouldReturnConflict_whenUsernameAlreadyExists() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("Rodion");
        when(userService.updateUsername(1L, "Rodion"))
                .thenThrow(new UserAlreadyExistsException(
                        "Пользователь с таким именем уже существует."
                ));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message")
                        .value("Пользователь с таким именем уже существует."));

        verify(userService).updateUsername(1L, "Rodion");
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() throws Exception {
        UserResponse response = createUserResponse(1L, "Yakov");
        when(userService.findByName("Yakov")).thenReturn(response);

        mockMvc.perform(get("/api/users/search")
                        .param("username", "Yakov"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("Yakov"));

        verify(userService).findByName("Yakov");
    }

    @Test
    void findByUsername_shouldReturnBadRequest_whenUsernameIsMissing() throws Exception {
        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Отсутствует обязательный параметр запроса: username"));

        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("username", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_shouldReturnBadRequest_whenUsernameIsTooShort() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("username", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_shouldReturnBadRequest_whenUsernameIsTooLong() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("username", "a".repeat(51)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userService.findByName("Unknown"))
                .thenThrow(new NotFoundException(
                        "Пользователь с именем (Unknown) не найден."
                ));

        mockMvc.perform(get("/api/users/search")
                        .param("username", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Пользователь с именем (Unknown) не найден."));

        verify(userService).findByName("Unknown");
    }
}
