package org.example.controller;

import org.example.dto.response.TopicResponse;
import org.example.exception.NotFoundException;
import org.example.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(TopicController.class)
@AutoConfigureMockMvc(addFilters = false)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicService topicService;

    private TopicResponse createTopicResponse(Long id, String name) {
        TopicResponse response = new TopicResponse();
        response.setId(id);
        response.setName(name);
        return response;
    }

    @Test
    void getAllTopics_shouldReturnTopics_whenTopicsExist() throws Exception {
        List<TopicResponse> topics = List.of(
                createTopicResponse(1L, "Java Core"),
                createTopicResponse(2L, "Spring")
        );

        when(topicService.getAllTopics()).thenReturn(topics);

        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Java Core"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Spring"));

        verify(topicService).getAllTopics();
    }

    @Test
    void getAllTopics_shouldReturnEmptyList_whenTopicsDoNotExist() throws Exception {
        when(topicService.getAllTopics()).thenReturn(List.of());

        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(topicService).getAllTopics();
    }

    @Test
    void getTopicById_shouldReturnTopic_whenTopicExists() throws Exception {
        TopicResponse response = createTopicResponse(1L, "Java Core");

        when(topicService.getByTopicId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/topics/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Core"));

        verify(topicService).getByTopicId(1L);
    }

    @Test
    void getTopicById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/topics/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID темы должен быть положительным числом."));

        verifyNoInteractions(topicService);
    }

    @Test
    void getTopicById_shouldReturnNotFound_whenTopicDoesNotExist() throws Exception {
        when(topicService.getByTopicId(1L))
                .thenThrow(new NotFoundException("Тема с id = 1 не найдена."));

        mockMvc.perform(get("/api/topics/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тема с id = 1 не найдена."));

        verify(topicService).getByTopicId(1L);
    }

    @Test
    void findByName_shouldReturnTopic_whenTopicExists() throws Exception {
        TopicResponse response = createTopicResponse(1L, "Java");

        when(topicService.getByTopicName("Java")).thenReturn(response);

        mockMvc.perform(get("/api/topics/search")
                        .param("topicName", "Java"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java"));

        verify(topicService).getByTopicName("Java");
    }

    @Test
    void findByName_shouldReturnBadRequest_whenTopicNameIsMissing() throws Exception {
        mockMvc.perform(get("/api/topics/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Отсутствует обязательный параметр запроса: topicName"));

        verifyNoInteractions(topicService);
    }

    @Test
    void findByName_shouldReturnBadRequest_whenTopicNameIsBlank() throws Exception {
        mockMvc.perform(get("/api/topics/search")
                        .param("topicName", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(topicService);
    }

    @Test
    void findByName_shouldReturnNotFound_whenTopicDoesNotExist() throws Exception {
        when(topicService.getByTopicName("Unknown"))
                .thenThrow(new NotFoundException("Тема с именем - Unknown - не найдена."));

        mockMvc.perform(get("/api/topics/search")
                        .param("topicName", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тема с именем - Unknown - не найдена."));

        verify(topicService).getByTopicName("Unknown");
    }
}
