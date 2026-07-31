package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.interview.AnswerRequest;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.interview.QuestionRequest;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.QuestionDifficulty;
import org.example.security.JwtService;
import org.example.service.InterviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterviewService interviewService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void questionResult_shouldReturnQuestionResult_whenDataIsValid() throws Exception {
        QuestionRequest request = new QuestionRequest("Java Core");
        request.setAiProfileId(4L);
        InterviewQuestionResult response = new InterviewQuestionResult(
                3L,
                1L,
                2L,
                4L,
                "Java Core",
                "Что такое JVM?",
                "interview",
                QuestionDifficulty.MEDIUM
        );

        when(jwtService.extractUserId(isNull(Jwt.class))).thenReturn(1L);
        when(interviewService.generateQuestion(1L, 4L, "Java Core")).thenReturn(response);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.questionId").value(3))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.topicId").value(2))
                .andExpect(jsonPath("$.aiProfileId").value(4))
                .andExpect(jsonPath("$.topicName").value("Java Core"))
                .andExpect(jsonPath("$.questionText").value("Что такое JVM?"))
                .andExpect(jsonPath("$.aiMode").value("interview"))
                .andExpect(jsonPath("$.difficulty").value("MEDIUM"));

        verify(jwtService).extractUserId(isNull(Jwt.class));
        verify(interviewService).generateQuestion(1L, 4L, "Java Core");
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenTopicIsBlank() throws Exception {
        QuestionRequest request = new QuestionRequest("   ");
        request.setAiProfileId(4L);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.topic")
                        .value("Тема не должна быть пустой."));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenTopicIsTooShort() throws Exception {
        QuestionRequest request = new QuestionRequest("a");
        request.setAiProfileId(4L);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.topic")
                        .value("Тема должна содержать от 2 до 200 символов."));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void questionResult_shouldReturnNotFound_whenServiceThrowsNotFound() throws Exception {
        QuestionRequest request = new QuestionRequest("Java Core");
        request.setAiProfileId(99L);

        when(jwtService.extractUserId(isNull(Jwt.class))).thenReturn(1L);
        when(interviewService.generateQuestion(1L, 99L, "Java Core"))
                .thenThrow(new NotFoundException("AI-профиль не найден."));

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("AI-профиль не найден."));

        verify(interviewService).generateQuestion(1L, 99L, "Java Core");
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenAiProfileIdIsMissing() throws Exception {
        QuestionRequest request = new QuestionRequest("Java Core");

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.aiProfileId")
                        .value("AI-профиль должен быть выбран."));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenAiProfileIdIsNotPositive() throws Exception {
        QuestionRequest request = new QuestionRequest("Java Core");
        request.setAiProfileId(0L);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.aiProfileId")
                        .value("ID AI-профиля должен быть положительным."));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void answerResult_shouldReturnAnswerResult_whenDataIsValid() throws Exception {
        AnswerRequest request = new AnswerRequest(2L, "JVM выполняет байткод.");
        InterviewAnswerResult response = new InterviewAnswerResult(
                1L,
                2L,
                3L,
                "Что такое JVM?",
                "JVM выполняет байткод.",
                "Ответ принят."
        );

        when(jwtService.extractUserId(isNull(Jwt.class))).thenReturn(1L);
        when(interviewService.submitUserAnswer(1L, 2L, "JVM выполняет байткод."))
                .thenReturn(response);

        mockMvc.perform(post("/api/interview/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.questionId").value(2))
                .andExpect(jsonPath("$.answerId").value(3))
                .andExpect(jsonPath("$.questionText").value("Что такое JVM?"))
                .andExpect(jsonPath("$.userAnswerText").value("JVM выполняет байткод."))
                .andExpect(jsonPath("$.feedback").value("Ответ принят."));

        verify(jwtService).extractUserId(isNull(Jwt.class));
        verify(interviewService).submitUserAnswer(1L, 2L, "JVM выполняет байткод.");
    }

    @Test
    void answerResult_shouldReturnBadRequest_whenAnswerTextIsBlank() throws Exception {
        AnswerRequest request = new AnswerRequest(2L, "   ");

        mockMvc.perform(post("/api/interview/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.textAnswer")
                        .value("Текст ответа не может быть пустым."));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void answerResult_shouldReturnBadRequest_whenQuestionIdIsNotPositive() throws Exception {
        AnswerRequest request = new AnswerRequest(0L, "Ответ");

        mockMvc.perform(post("/api/interview/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.questionId")
                        .value("ID вопроса должен быть положительным числом"));

        verifyNoInteractions(jwtService, interviewService);
    }

    @Test
    void answerResult_shouldReturnBadRequest_whenServiceThrowsBadRequest() throws Exception {
        AnswerRequest request = new AnswerRequest(2L, "Ответ");

        when(jwtService.extractUserId(isNull(Jwt.class))).thenReturn(1L);
        when(interviewService.submitUserAnswer(1L, 2L, "Ответ"))
                .thenThrow(new BadRequestException("Нельзя ответить на вопрос другого пользователя."));

        mockMvc.perform(post("/api/interview/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Нельзя ответить на вопрос другого пользователя."));

        verify(interviewService).submitUserAnswer(1L, 2L, "Ответ");
    }
}
