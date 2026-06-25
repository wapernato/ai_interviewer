package org.example.controller;

import org.example.dto.user.UserHistoryItem;
import org.example.exception.NotFoundException;
import org.example.service.UserHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserHistoryController.class)
class UserHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserHistoryService userHistoryService;

    @Test
    void userHistory_shouldReturnHistory_whenUserExists() throws Exception {
        List<UserHistoryItem> history = List.of(
                new UserHistoryItem(
                        1L,
                        "Yakov",
                        "Java",
                        "Что такое JVM?",
                        "Java Virtual Machine",
                        "mock-ai"
                )
        );

        when(userHistoryService.findHistoryByUserId(1L)).thenReturn(history);

        mockMvc.perform(get("/api/users/1/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].questionId").value(1))
                .andExpect(jsonPath("$[0].username").value("Yakov"))
                .andExpect(jsonPath("$[0].topicName").value("Java"))
                .andExpect(jsonPath("$[0].textQuestion").value("Что такое JVM?"))
                .andExpect(jsonPath("$[0].answerText").value("Java Virtual Machine"))
                .andExpect(jsonPath("$[0].modelName").value("mock-ai"));

        verify(userHistoryService).findHistoryByUserId(1L);
    }

    @Test
    void userHistory_shouldReturnEmptyList_whenHistoryDoesNotExist() throws Exception {
        when(userHistoryService.findHistoryByUserId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/1/history"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(userHistoryService).findHistoryByUserId(1L);
    }

    @Test
    void userHistory_shouldReturnBadRequest_whenUserIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/users/0/history"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.userId")
                        .value("ID пользователя должен быть положительным числом."));

        verifyNoInteractions(userHistoryService);
    }

    @Test
    void userHistory_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        when(userHistoryService.findHistoryByUserId(1L))
                .thenThrow(new NotFoundException("Пользователь с id=1 не найден."));

        mockMvc.perform(get("/api/users/1/history"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=1 не найден."));

        verify(userHistoryService).findHistoryByUserId(1L);
    }
}
