package org.example.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.aiprofile.CreateAiProfileRequest;
import org.example.dto.response.AiProfileResponse;
import org.example.exception.AiProfileAlreadyExistsException;
import org.example.exception.NotFoundException;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAiProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminAiProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AiProfileService aiProfileService;

    private CreateAiProfileRequest createRequest() {
        return new CreateAiProfileRequest(
                "interview",
                "Профиль для интервью",
                "Задавай вопросы по Java Backend",
                "mock-ai",
                "ru",
                "detailed",
                "medium",
                "detailed",
                false,
                true,
                0.7,
                1000
        );
    }

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
    void addNewProfile_shouldReturnProfile_whenDataIsValid() throws Exception {
        CreateAiProfileRequest request = createRequest();
        AiProfileResponse response = createResponse(1L, "interview", true);

        when(aiProfileService.addProfile(any(AiProfile.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/ai-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mode").value("interview"))
                .andExpect(jsonPath("$.modelName").value("mock-ai"))
                .andExpect(jsonPath("$.language").value("ru"))
                .andExpect(jsonPath("$.active").value(true));

        verify(aiProfileService).addProfile(argThat(profile ->
                "interview".equals(profile.getMode())
                        && "mock-ai".equals(profile.getModelName())
                        && "ru".equals(profile.getLanguage())
                        && Boolean.TRUE.equals(profile.getActive())
        ));
    }

    @Test
    void addNewProfile_shouldReturnBadRequest_whenModeIsBlank() throws Exception {
        CreateAiProfileRequest request = createRequest();
        request.setMode("   ");

        mockMvc.perform(post("/api/admin/ai-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.mode")
                        .value("Режим AI-профиля не может быть пустым."));

        verifyNoInteractions(aiProfileService);
    }

    @Test
    void addNewProfile_shouldReturnBadRequest_whenLanguageIsInvalid() throws Exception {
        CreateAiProfileRequest request = createRequest();
        request.setLanguage("de");

        mockMvc.perform(post("/api/admin/ai-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.language")
                        .value("Язык должен быть 'ru' или 'en'."));

        verifyNoInteractions(aiProfileService);
    }

    @Test
    void addNewProfile_shouldReturnConflict_whenModeAlreadyExists() throws Exception {
        CreateAiProfileRequest request = createRequest();

        when(aiProfileService.addProfile(any(AiProfile.class)))
                .thenThrow(new AiProfileAlreadyExistsException("AI-профиль с таким mode уже существует."));

        mockMvc.perform(post("/api/admin/ai-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("AI-профиль с таким mode уже существует."));

        verify(aiProfileService).addProfile(any(AiProfile.class));
    }

    @Test
    void updateAiProfile_shouldReturnProfile_whenDataIsValid() throws Exception {
        CreateAiProfileRequest request = createRequest();
        AiProfileResponse response = createResponse(1L, "interview", true);

        when(aiProfileService.updateProfile(any(AiProfile.class))).thenReturn(response);

        mockMvc.perform(put("/api/admin/ai-profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mode").value("interview"));

        verify(aiProfileService).updateProfile(argThat(profile ->
                Long.valueOf(1L).equals(profile.getId())
                        && "interview".equals(profile.getMode())
                        && "mock-ai".equals(profile.getModelName())
        ));
    }

    @Test
    void updateAiProfile_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        CreateAiProfileRequest request = createRequest();

        mockMvc.perform(put("/api/admin/ai-profiles/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID AI-профиля должен быть положительным числом."));

        verifyNoInteractions(aiProfileService);
    }

    @Test
    void updateAiProfile_shouldReturnNotFound_whenProfileDoesNotExist() throws Exception {
        CreateAiProfileRequest request = createRequest();

        when(aiProfileService.updateProfile(any(AiProfile.class)))
                .thenThrow(new NotFoundException("AI-профиль по id=1 не найден."));

        mockMvc.perform(put("/api/admin/ai-profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("AI-профиль по id=1 не найден."));

        verify(aiProfileService).updateProfile(any(AiProfile.class));
    }

    @Test
    void deleteByAiProfileId_shouldReturnNoContent_whenProfileExists() throws Exception {
        mockMvc.perform(delete("/api/admin/ai-profiles/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(aiProfileService).deleteById(1L);
    }

    @Test
    void deleteByAiProfileId_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(delete("/api/admin/ai-profiles/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID AI-профиля должен быть положительным числом."));

        verifyNoInteractions(aiProfileService);
    }

    @Test
    void deleteByAiProfileId_shouldReturnNotFound_whenProfileDoesNotExist() throws Exception {
        doThrow(new NotFoundException("AI-профиль по id=1 не найден."))
                .when(aiProfileService).deleteById(1L);

        mockMvc.perform(delete("/api/admin/ai-profiles/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("AI-профиль по id=1 не найден."));

        verify(aiProfileService).deleteById(1L);
    }

    @Test
    void activeAiProfile_shouldReturnProfile_whenProfileExists() throws Exception {
        AiProfileResponse response = createResponse(1L, "interview", true);

        when(aiProfileService.activateProfile(1L)).thenReturn(response);

        mockMvc.perform(put("/api/admin/ai-profiles/1/activate"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(true));

        verify(aiProfileService).activateProfile(1L);
    }

    @Test
    void activeAiProfile_shouldReturnBadRequest_whenIdIsNotPositive() throws Exception {
        mockMvc.perform(put("/api/admin/ai-profiles/0/activate"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.validationErrors.id")
                        .value("ID AI-профиля должен быть положительным числом."));

        verifyNoInteractions(aiProfileService);
    }
}
