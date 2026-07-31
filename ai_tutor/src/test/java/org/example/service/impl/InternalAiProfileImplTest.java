package org.example.service.impl;

import org.example.dto.response.InternalAiProfileConfigResponse;
import org.example.exception.AiProfileUnavailableException;
import org.example.exception.NotFoundException;
import org.example.mapper.InternalAiProfileMapper;
import org.example.model.AiProfile;
import org.example.repository.AiProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalAiProfileImplTest {

    @Mock
    private AiProfileRepository aiProfileRepository;

    @Mock
    private InternalAiProfileMapper internalAiProfileMapper;

    private InternalAiProfileImpl internalAiProfile;

    @BeforeEach
    void setUp() {
        internalAiProfile = new InternalAiProfileImpl(
                aiProfileRepository,
                internalAiProfileMapper
        );
    }

    @Test
    void getActiveProfileConfig_shouldReturnConfig_whenProfileIsActive() {
        AiProfile profile = new AiProfile();
        profile.setId(1L);
        profile.setActive(true);

        InternalAiProfileConfigResponse expectedResponse =
                new InternalAiProfileConfigResponse(
                        1L,
                        "strict",
                        "instruction",
                        "model",
                        "ru",
                        "detailed",
                        "hard",
                        "strict",
                        false,
                        0.7,
                        1000
                );

        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(internalAiProfileMapper.toInternalConfigResponse(profile))
                .thenReturn(expectedResponse);

        InternalAiProfileConfigResponse result =
                internalAiProfile.getActiveProfileConfig(1L);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getActiveProfileConfig_shouldThrowConflict_whenProfileIsInactive() {
        AiProfile profile = new AiProfile();
        profile.setId(2L);
        profile.setActive(false);

        when(aiProfileRepository.findById(2L)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> internalAiProfile.getActiveProfileConfig(2L))
                .isInstanceOf(AiProfileUnavailableException.class)
                .hasMessage("AI-профиль с id=2 недоступен.");

        verifyNoInteractions(internalAiProfileMapper);
    }

    @Test
    void getActiveProfileConfig_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> internalAiProfile.getActiveProfileConfig(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль с id=99 не найден.");

        verifyNoInteractions(internalAiProfileMapper);
    }
}
