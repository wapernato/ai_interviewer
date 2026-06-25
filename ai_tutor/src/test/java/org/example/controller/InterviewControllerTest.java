package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.interview.AnswerRequest;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.interview.QuestionRequest;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.service.InterviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewController.class)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterviewService interviewService;

    @Test
    void questionResult_shouldReturnQuestionResult_whenDataIsValid() throws Exception {
        QuestionRequest request = new QuestionRequest(1L, 2L);
        InterviewQuestionResult response = new InterviewQuestionResult(
                3L,
                1L,
                2L,
                4L,
                "Java",
                "Что такое JVM?",
                "interview",
                "medium"
        );

        when(interviewService.generateQuestion(1L, 2L)).thenReturn(response);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.questionId").value(3))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.topicId").value(2))
                .andExpect(jsonPath("$.aiProfileId").value(4))
                .andExpect(jsonPath("$.topicName").value("Java"))
                .andExpect(jsonPath("$.questionText").value("Что такое JVM?"))
                .andExpect(jsonPath("$.aiMode").value("interview"))
                .andExpect(jsonPath("$.difficulty").value("medium"));

        verify(interviewService).generateQuestion(1L, 2L);
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenUserIdIsNull() throws Exception {
        QuestionRequest request = new QuestionRequest(null, 2L);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.userId")
                        .value("ID пользователя не должен быть пустым."));

        verifyNoInteractions(interviewService);
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenTopicIdIsNotPositive() throws Exception {
        QuestionRequest request = new QuestionRequest(1L, 0L);

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.topicId")
                        .value("ID темы должно быть положительным числом"));

        verifyNoInteractions(interviewService);
    }

    @Test
    void questionResult_shouldReturnNotFound_whenServiceThrowsNotFound() throws Exception {
        QuestionRequest request = new QuestionRequest(1L, 2L);

        when(interviewService.generateQuestion(1L, 2L))
                .thenThrow(new NotFoundException("Тема не найдена."));

        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Тема не найдена."));

        verify(interviewService).generateQuestion(1L, 2L);
    }

    @Test
    void questionResult_shouldReturnBadRequest_whenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/interview/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Некорректное тело запроса."));

        verifyNoInteractions(interviewService);
    }

    @Test
    void answerResult_shouldReturnAnswerResult_whenDataIsValid() throws Exception {
        AnswerRequest request = new AnswerRequest(1L, 2L, "JVM выполняет байткод.");
        InterviewAnswerResult response = new InterviewAnswerResult(
                1L,
                2L,
                3L,
                "Что такое JVM?",
                "JVM выполняет байткод.",
                "Ответ принят."
        );

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

        verify(interviewService).submitUserAnswer(1L, 2L, "JVM выполняет байткод.");
    }

    @Test
    void answerResult_shouldReturnBadRequest_whenAnswerTextIsBlank() throws Exception {
        AnswerRequest request = new AnswerRequest(1L, 2L, "   ");

        mockMvc.perform(post("/api/interview/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.textAnswer")
                        .value("Текст ответа не может быть пустым."));

        verifyNoInteractions(interviewService);
    }

    @Test
    void answerResult_shouldReturnBadRequest_whenQuestionIdIsNotPositive() throws Exception {
        AnswerRequest request = new AnswerRequest(1L, 0L, "Ответ");

        mockMvc.perform(post("/api/interview/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.questionId")
                        .value("ID вопроса должен быть положительным числом"));

        verifyNoInteractions(interviewService);
    }

    @Test
    void answerResult_shouldReturnBadRequest_whenServiceThrowsBadRequest() throws Exception {
        AnswerRequest request = new AnswerRequest(1L, 2L, "Ответ");

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
