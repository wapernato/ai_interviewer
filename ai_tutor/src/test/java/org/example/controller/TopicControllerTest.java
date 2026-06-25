package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.response.TopicResponse;
import org.example.dto.topic.CreateTopicRequest;
import org.example.dto.topic.UpdateTopicRequest;
import org.example.exception.NotFoundException;
import org.example.exception.TopicAlreadyExistsException;
import org.example.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TopicController.class)
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void createTopic_shouldReturnTopic_whenDataIsValid() throws Exception {
        CreateTopicRequest request = new CreateTopicRequest("Java");
        TopicResponse response = createTopicResponse(1L, "Java");

        when(topicService.addTopic("Java")).thenReturn(response);

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java"));

        verify(topicService).addTopic("Java");
    }

    @Test
    void createTopic_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        CreateTopicRequest request = new CreateTopicRequest("   ");

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Название темы не должно быть пустым."));

        verifyNoInteractions(topicService);
    }

    @Test
    void createTopic_shouldReturnBadRequest_whenNameIsTooShort() throws Exception {
        CreateTopicRequest request = new CreateTopicRequest("a");

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.name")
                        .value("Длина названия темы должна быть от 2 до 100 символов."));

        verifyNoInteractions(topicService);
    }

    @Test
    void createTopic_shouldReturnConflict_whenTopicAlreadyExists() throws Exception {
        CreateTopicRequest request = new CreateTopicRequest("Java");

        when(topicService.addTopic("Java"))
                .thenThrow(new TopicAlreadyExistsException("Тема с названием 'Java' уже существует."));

        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Тема с названием 'Java' уже существует."));

        verify(topicService).addTopic("Java");
    }

    @Test
    void createTopic_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(topicService);
    }

    @Test
    void deleteByTopicId_shouldReturnNoContent_whenTopicExists() throws Exception {
        mockMvc.perform(delete("/api/topics/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(topicService).deleteByTopicId(1L);
    }

    @Test
    void deleteByTopicId_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(delete("/api/topics/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID темы должен быть положительным числом."));

        verifyNoInteractions(topicService);
    }

    @Test
    void deleteByTopicId_shouldReturnNotFound_whenTopicDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Тема с id = 1 не найдена."))
                .when(topicService).deleteByTopicId(1L);

        mockMvc.perform(delete("/api/topics/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тема с id = 1 не найдена."));

        verify(topicService).deleteByTopicId(1L);
    }

    @Test
    void updateTopicById_shouldReturnTopic_whenDataIsValid() throws Exception {
        UpdateTopicRequest request = new UpdateTopicRequest();
        request.setName("Spring");
        TopicResponse response = createTopicResponse(1L, "Spring");

        when(topicService.updateTopic(1L, "Spring")).thenReturn(response);

        mockMvc.perform(put("/api/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Spring"));

        verify(topicService).updateTopic(1L, "Spring");
    }

    @Test
    void updateTopicById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        UpdateTopicRequest request = new UpdateTopicRequest();
        request.setName("Spring");

        mockMvc.perform(put("/api/topics/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID темы должен быть положительным числом."));

        verifyNoInteractions(topicService);
    }

    @Test
    void updateTopicById_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        UpdateTopicRequest request = new UpdateTopicRequest();
        request.setName("   ");

        mockMvc.perform(put("/api/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));

        verifyNoInteractions(topicService);
    }

    @Test
    void updateTopicById_shouldReturnNotFound_whenTopicDoesNotExist() throws Exception {
        UpdateTopicRequest request = new UpdateTopicRequest();
        request.setName("Spring");

        when(topicService.updateTopic(1L, "Spring"))
                .thenThrow(new NotFoundException("Тема с id = 1 не найдена."));

        mockMvc.perform(put("/api/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тема с id = 1 не найдена."));

        verify(topicService).updateTopic(1L, "Spring");
    }

    @Test
    void updateTopicById_shouldReturnConflict_whenNameAlreadyExists() throws Exception {
        UpdateTopicRequest request = new UpdateTopicRequest();
        request.setName("Spring");

        when(topicService.updateTopic(1L, "Spring"))
                .thenThrow(new TopicAlreadyExistsException("Тема с таким названием уже существует."));

        mockMvc.perform(put("/api/topics/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Тема с таким названием уже существует."));

        verify(topicService).updateTopic(1L, "Spring");
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
