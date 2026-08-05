package org.example.controller;

import org.example.dto.response.aiprofile.AiProfileResponse;
import org.example.dto.response.aiprofile.AvailableAiProfileResponse;
import org.example.exception.NotFoundException;
import org.example.service.AiProfileService;
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

@WebMvcTest(AiProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiProfileService aiProfileService;

    private AiProfileResponse createResponse(Long id, String mode, boolean active) {
        AiProfileResponse response = new AiProfileResponse();
        response.setId(id);
        response.setMode(mode);
        response.setDescriptionMode("Профиль для интервью");
        response.setInstructionMode("Задавай вопросы по Java Backend");
        response.setModelName("mock-ai");
        response.setLanguage("ru");
        response.setAnswerStyle("detailed");
        response.setDifficulty("medium");
        response.setFeedbackMode("detailed");
        response.setHintMode(false);
        response.setActive(active);
        response.setTemperature(0.7);
        response.setMaxTokens(1000);
        return response;
    }

    @Test
    void getAllAiProfile_shouldReturnProfiles_whenProfilesExist() throws Exception {
        List<AiProfileResponse> profiles = List.of(
                createResponse(1L, "interview", true),
                createResponse(2L, "practice", false)
        );

        when(aiProfileService.getAllProfiles()).thenReturn(profiles);

        mockMvc.perform(get("/api/ai-profiles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mode").value("interview"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].mode").value("practice"))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(aiProfileService).getAllProfiles();
    }

    @Test
    void getAvailableProfiles_shouldReturnOnlyPublicProfileFields() throws Exception {
        List<AvailableAiProfileResponse> profiles = List.of(
                new AvailableAiProfileResponse(
                        1L,
                        "strict-interviewer",
                        "Строгое техническое интервью",
                        "hard",
                        false
                )
        );

        when(aiProfileService.getAllAvailableAiProfileResponse()).thenReturn(profiles);

        mockMvc.perform(get("/api/ai-profiles/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("strict-interviewer"))
                .andExpect(jsonPath("$[0].description").value("Строгое техническое интервью"))
                .andExpect(jsonPath("$[0].difficulty").value("hard"))
                .andExpect(jsonPath("$[0].hintsEnable").value(false))
                .andExpect(jsonPath("$[0].instructionMode").doesNotExist())
                .andExpect(jsonPath("$[0].modelName").doesNotExist())
                .andExpect(jsonPath("$[0].temperature").doesNotExist());

        verify(aiProfileService).getAllAvailableAiProfileResponse();
    }

    @Test
    void getAiProfileById_shouldReturnProfile_whenProfileExists() throws Exception {
        AiProfileResponse response = createResponse(1L, "interview", true);

        when(aiProfileService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/ai-profiles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mode").value("interview"))
                .andExpect(jsonPath("$.language").value("ru"))
                .andExpect(jsonPath("$.active").value(true));

        verify(aiProfileService).getById(1L);
    }

    @Test
    void getAiProfileById_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/ai-profiles/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID AI-профиля должен быть положительным числом."));

        verifyNoInteractions(aiProfileService);
    }

    @Test
    void getAiProfileById_shouldReturnNotFound_whenProfileDoesNotExist() throws Exception {
        when(aiProfileService.getById(1L))
                .thenThrow(new NotFoundException("AI-профиль по id=1 не найден."));

        mockMvc.perform(get("/api/ai-profiles/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("AI-профиль по id=1 не найден."));

        verify(aiProfileService).getById(1L);
    }

    @Test
    void findByMode_shouldReturnProfile_whenModeExists() throws Exception {
        AiProfileResponse response = createResponse(1L, "interview", true);

        when(aiProfileService.getByMode("interview")).thenReturn(response);

        mockMvc.perform(get("/api/ai-profiles/search")
                        .param("mode", "interview"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mode").value("interview"));

        verify(aiProfileService).getByMode("interview");
    }

    @Test
    void findByMode_shouldReturnBadRequest_whenModeIsMissing() throws Exception {
        mockMvc.perform(get("/api/ai-profiles/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .value("Отсутствует обязательный параметр запроса: mode"));

        verifyNoInteractions(aiProfileService);
    }

    @Test
    void findByActive_shouldReturnProfiles_whenActiveParameterExists() throws Exception {
        List<AiProfileResponse> profiles = List.of(createResponse(1L, "interview", true));

        when(aiProfileService.findAllProfiles(true)).thenReturn(profiles);

        mockMvc.perform(get("/api/ai-profiles/filter")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(aiProfileService).findAllProfiles(true);
    }

    @Test
    void findByLanguage_shouldReturnProfile_whenLanguageExists() throws Exception {
        AiProfileResponse response = createResponse(1L, "interview", true);

        when(aiProfileService.getByLanguage("ru")).thenReturn(response);

        mockMvc.perform(get("/api/ai-profiles/language")
                        .param("language", "ru"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.language").value("ru"));

        verify(aiProfileService).getByLanguage("ru");
    }
}
