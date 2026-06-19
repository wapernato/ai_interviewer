package org.example.service.impl;

import org.example.dto.response.AiProfileResponse;
import org.example.exception.AiProfileAlreadyExistsException;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.AiProfileMapper;
import org.example.model.AiProfile;
import org.example.repository.AiProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiProfileServiceImplTest {

    @Mock
    private AiProfileRepository aiProfileRepository;

    private AiProfileServiceImpl aiProfileService;

    @BeforeEach
    void setUp() {
        aiProfileService = new AiProfileServiceImpl(
                aiProfileRepository,
                new AiProfileMapper()
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenAiProfileIsNull() {
        assertThatThrownBy(() -> aiProfileService.addProfile(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("AI-профиль не должен быть null.");

        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenModeIsNull() {
        assertInvalidProfile(
                profile -> profile.setMode(null),
                "Mode AI-профиля не должен быть пустым."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenModeIsTooShort() {
        assertInvalidProfile(
                profile -> profile.setMode("a"),
                "Mode AI-профиля должен быть от 2 до 100 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenModeIsTooLong() {
        assertInvalidProfile(
                profile -> profile.setMode("a".repeat(101)),
                "Mode AI-профиля должен быть от 2 до 100 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenInstructionIsNull() {
        assertInvalidProfile(
                profile -> profile.setInstructionMode(null),
                "Инструкция AI-профиля не должна быть пустой."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenInstructionIsTooShort() {
        assertInvalidProfile(
                profile -> profile.setInstructionMode("short"),
                "Инструкция AI-профиля слишком короткая."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenModelNameIsTooLong() {
        assertInvalidProfile(
                profile -> profile.setModelName("a".repeat(101)),
                "Название модели не должно быть длиннее 100 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenLanguageIsTooLong() {
        assertInvalidProfile(
                profile -> profile.setLanguage("a".repeat(21)),
                "Название языка не должно быть длиннее 20 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenAnswerStyleIsTooLong() {
        assertInvalidProfile(
                profile -> profile.setAnswerStyle("a".repeat(51)),
                "Стиль ответа не должен быть длиннее 50 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenDifficultyIsTooLong() {
        assertInvalidProfile(
                profile -> profile.setDifficulty("a".repeat(31)),
                "Сложность не должна быть длиннее 30 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenFeedbackModeIsTooLong() {
        assertInvalidProfile(
                profile -> profile.setFeedbackMode("a".repeat(51)),
                "Режим обратной связи не должен быть длиннее 50 символов."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenTemperatureIsNegative() {
        assertInvalidProfile(
                profile -> profile.setTemperature(-0.1),
                "Temperature должна быть в диапазоне от 0 до 2."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenTemperatureIsAboveTwo() {
        assertInvalidProfile(
                profile -> profile.setTemperature(2.1),
                "Temperature должна быть в диапазоне от 0 до 2."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenMaxTokensIsNotPositive() {
        assertInvalidProfile(
                profile -> profile.setMaxTokens(0),
                "Max tokens должен быть больше 0."
        );
    }

    @Test
    void addProfile_shouldThrowBadRequest_whenMaxTokensIsAboveLimit() {
        assertInvalidProfile(
                profile -> profile.setMaxTokens(4001),
                "Max tokens пока не должен быть больше 4000."
        );
    }

    @Test
    void addProfile_shouldThrowAiProfileAlreadyExists_whenModeAlreadyExists() {
        AiProfile newProfile = createAiProfile();
        AiProfile existingProfile = createSavedProfile(2L, "interview");
        when(aiProfileRepository.findByMode("interview"))
                .thenReturn(Optional.of(existingProfile));

        assertThatThrownBy(() -> aiProfileService.addProfile(newProfile))
                .isInstanceOf(AiProfileAlreadyExistsException.class)
                .hasMessage("AI-профиль с таким mode уже существует.");

        verify(aiProfileRepository, never()).findAll();
        verify(aiProfileRepository, never()).save(any(AiProfile.class));
    }

    @Test
    void addProfile_shouldApplyDefaultsAndTrimFields_whenOptionalFieldsAreMissing() {
        AiProfile newProfile = createAiProfile();
        newProfile.setMode("  interview  ");
        newProfile.setDescriptionMode("  description  ");
        newProfile.setInstructionMode("  Valid instruction  ");
        newProfile.setModelName(null);
        newProfile.setLanguage("   ");
        newProfile.setAnswerStyle(null);
        newProfile.setDifficulty(null);
        newProfile.setFeedbackMode(null);
        newProfile.setHintMode(null);
        newProfile.setActive(null);
        newProfile.setTemperature(null);
        newProfile.setMaxTokens(null);

        when(aiProfileRepository.findByMode("interview")).thenReturn(Optional.empty());
        when(aiProfileRepository.save(any(AiProfile.class)))
                .thenAnswer(invocation -> {
                    AiProfile profile = invocation.getArgument(0);
                    profile.setId(1L);
                    return profile;
                });

        AiProfileResponse result = aiProfileService.addProfile(newProfile);

        assertThat(result.getMode()).isEqualTo("interview");
        assertThat(result.getDescriptionMode()).isEqualTo("description");
        assertThat(result.getInstructionMode()).isEqualTo("Valid instruction");
        assertThat(result.getModelName()).isEqualTo("mock-ai");
        assertThat(result.getLanguage()).isEqualTo("ru");
        assertThat(result.getAnswerStyle()).isEqualTo("detailed");
        assertThat(result.getDifficulty()).isEqualTo("medium");
        assertThat(result.getFeedbackMode()).isEqualTo("detailed");
        assertThat(result.getHintMode()).isFalse();
        assertThat(result.getActive()).isFalse();
        assertThat(result.getTemperature()).isEqualTo(0.70);
        assertThat(result.getMaxTokens()).isEqualTo(1000);
    }

    @Test
    void addProfile_shouldDeactivateOtherProfiles_whenNewProfileIsActive() {
        AiProfile newProfile = createAiProfile();
        newProfile.setActive(true);
        AiProfile activeProfile = createSavedProfile(2L, "old-mode");
        activeProfile.setActive(true);

        when(aiProfileRepository.findByMode("interview")).thenReturn(Optional.empty());
        when(aiProfileRepository.findAll()).thenReturn(List.of(activeProfile));
        when(aiProfileRepository.save(newProfile)).thenReturn(newProfile);

        AiProfileResponse result = aiProfileService.addProfile(newProfile);

        assertThat(activeProfile.getActive()).isFalse();
        assertThat(result.getActive()).isTrue();
        verify(aiProfileRepository).findAll();
        verify(aiProfileRepository).save(newProfile);
    }

    @Test
    void addProfile_shouldReturnSavedProfile_whenDataIsValid() {
        AiProfile newProfile = createAiProfile();
        AiProfile savedProfile = createSavedProfile(1L, "interview");
        when(aiProfileRepository.findByMode("interview")).thenReturn(Optional.empty());
        when(aiProfileRepository.save(newProfile)).thenReturn(savedProfile);

        AiProfileResponse result = aiProfileService.addProfile(newProfile);

        assertAiProfileResponse(result, savedProfile);
        verify(aiProfileRepository).save(newProfile);
        verify(aiProfileRepository, never()).findAll();
    }

    @Test
    void getById_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> aiProfileService.getById(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Некорректный id.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void getById_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.getById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль по id=1 не найден.");
    }

    @Test
    void getById_shouldReturnProfile_whenProfileExists() {
        AiProfile profile = createSavedProfile(1L, "interview");
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        assertAiProfileResponse(aiProfileService.getById(1L), profile);
    }

    @Test
    void getByMode_shouldThrowBadRequest_whenModeIsBlank() {
        assertThatThrownBy(() -> aiProfileService.getByMode("   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Mode AI-профиля не должен быть пустым.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void getByMode_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.findByMode("interview")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.getByMode("  interview  "))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Мод с названием (interview) не найден.");
    }

    @Test
    void getByMode_shouldReturnProfile_whenProfileExists() {
        AiProfile profile = createSavedProfile(1L, "interview");
        when(aiProfileRepository.findByMode("interview")).thenReturn(Optional.of(profile));

        AiProfileResponse result = aiProfileService.getByMode("  interview  ");

        assertAiProfileResponse(result, profile);
        verify(aiProfileRepository).findByMode("interview");
    }

    @Test
    void getAllProfiles_shouldReturnAllProfiles() {
        when(aiProfileRepository.findAll()).thenReturn(List.of(
                createSavedProfile(1L, "interview"),
                createSavedProfile(2L, "mentor")
        ));

        List<AiProfileResponse> result = aiProfileService.getAllProfiles();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AiProfileResponse::getMode)
                .containsExactly("interview", "mentor");
    }

    @Test
    void getAllProfiles_shouldReturnEmptyList_whenProfilesDoNotExist() {
        when(aiProfileRepository.findAll()).thenReturn(List.of());

        assertThat(aiProfileService.getAllProfiles()).isEmpty();
    }

    @Test
    void updateProfile_shouldThrowBadRequest_whenProfileIsNull() {
        assertThatThrownBy(() -> aiProfileService.updateProfile(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("AI-профиль не должен быть null.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void updateProfile_shouldThrowBadRequest_whenIdIsInvalid() {
        AiProfile profile = createAiProfile();
        profile.setId(0L);

        assertThatThrownBy(() -> aiProfileService.updateProfile(profile))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Некорректный id.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void updateProfile_shouldThrowNotFound_whenProfileDoesNotExist() {
        AiProfile profile = createSavedProfile(1L, "interview");
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.updateProfile(profile))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль с id=1 не найден.");

        verify(aiProfileRepository, never()).save(any(AiProfile.class));
    }

    @Test
    void updateProfile_shouldThrowBadRequest_whenProfileIsInvalid() {
        AiProfile profile = createSavedProfile(1L, "interview");
        profile.setTemperature(2.1);
        when(aiProfileRepository.findById(1L))
                .thenReturn(Optional.of(createSavedProfile(1L, "old-mode")));

        assertThatThrownBy(() -> aiProfileService.updateProfile(profile))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Temperature должна быть в диапазоне от 0 до 2.");

        verify(aiProfileRepository, never()).findByMode(anyString());
        verify(aiProfileRepository, never()).save(any(AiProfile.class));
    }

    @Test
    void updateProfile_shouldThrowAiProfileAlreadyExists_whenModeBelongsToAnotherProfile() {
        AiProfile update = createSavedProfile(1L, "mentor");
        AiProfile oldProfile = createSavedProfile(1L, "interview");
        AiProfile profileWithSameMode = createSavedProfile(2L, "mentor");
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(oldProfile));
        when(aiProfileRepository.findByMode("mentor"))
                .thenReturn(Optional.of(profileWithSameMode));

        assertThatThrownBy(() -> aiProfileService.updateProfile(update))
                .isInstanceOf(AiProfileAlreadyExistsException.class)
                .hasMessage("AI-профиль с таким mode уже существует.");

        verify(aiProfileRepository, never()).save(any(AiProfile.class));
    }

    @Test
    void updateProfile_shouldAllowModeThatBelongsToSameProfile() {
        AiProfile update = createSavedProfile(1L, "interview");
        update.setDifficulty("senior");
        AiProfile oldProfile = createSavedProfile(1L, "interview");
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(oldProfile));
        when(aiProfileRepository.findByMode("interview")).thenReturn(Optional.of(oldProfile));
        when(aiProfileRepository.save(oldProfile)).thenReturn(oldProfile);

        AiProfileResponse result = aiProfileService.updateProfile(update);

        assertThat(result.getDifficulty()).isEqualTo("senior");
        verify(aiProfileRepository).save(oldProfile);
    }

    @Test
    void updateProfile_shouldDeactivateOthersAndReturnUpdatedProfile_whenProfileBecomesActive() {
        AiProfile update = createSavedProfile(1L, "mentor");
        update.setActive(true);
        AiProfile oldProfile = createSavedProfile(1L, "interview");
        AiProfile anotherProfile = createSavedProfile(2L, "old-active");
        anotherProfile.setActive(true);

        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(oldProfile));
        when(aiProfileRepository.findByMode("mentor")).thenReturn(Optional.empty());
        when(aiProfileRepository.findAll()).thenReturn(List.of(oldProfile, anotherProfile));
        when(aiProfileRepository.save(oldProfile)).thenReturn(oldProfile);

        AiProfileResponse result = aiProfileService.updateProfile(update);

        assertThat(result.getMode()).isEqualTo("mentor");
        assertThat(result.getActive()).isTrue();
        assertThat(anotherProfile.getActive()).isFalse();
        verify(aiProfileRepository).findAll();
        verify(aiProfileRepository).save(oldProfile);
    }

    @Test
    void deleteById_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> aiProfileService.deleteById(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Некорректный id.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void deleteById_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.deleteById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль с таким id не найден.");

        verify(aiProfileRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_shouldDeleteProfile_whenProfileExists() {
        when(aiProfileRepository.findById(1L))
                .thenReturn(Optional.of(createSavedProfile(1L, "interview")));

        aiProfileService.deleteById(1L);

        verify(aiProfileRepository).deleteById(1L);
    }

    @Test
    void getActiveProfile_shouldThrowNotFound_whenActiveProfileDoesNotExist() {
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.getActiveProfile())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Активный AI-профиль не найден.");
    }

    @Test
    void getActiveProfile_shouldReturnActiveProfile_whenItExists() {
        AiProfile profile = createSavedProfile(1L, "interview");
        profile.setActive(true);
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.of(profile));

        AiProfileResponse result = aiProfileService.getActiveProfile();

        assertAiProfileResponse(result, profile);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    void activateProfile_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> aiProfileService.activateProfile(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Некорректный id.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void activateProfile_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.activateProfile(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль по id = 1 не найден.");

        verify(aiProfileRepository, never()).findAll();
        verify(aiProfileRepository, never()).save(any(AiProfile.class));
    }

    @Test
    void activateProfile_shouldDeactivateOthersAndActivateSelectedProfile() {
        AiProfile selectedProfile = createSavedProfile(1L, "interview");
        AiProfile anotherProfile = createSavedProfile(2L, "mentor");
        anotherProfile.setActive(true);
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(selectedProfile));
        when(aiProfileRepository.findAll()).thenReturn(List.of(selectedProfile, anotherProfile));
        when(aiProfileRepository.save(selectedProfile)).thenReturn(selectedProfile);

        AiProfileResponse result = aiProfileService.activateProfile(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getActive()).isTrue();
        assertThat(selectedProfile.getActive()).isTrue();
        assertThat(anotherProfile.getActive()).isFalse();
        verify(aiProfileRepository).findAll();
        verify(aiProfileRepository).save(selectedProfile);
    }

    @Test
    void findAllProfiles_shouldReturnProfilesWithRequestedActiveStatus() {
        AiProfile activeProfile = createSavedProfile(1L, "interview");
        activeProfile.setActive(true);
        when(aiProfileRepository.findByActive(true)).thenReturn(List.of(activeProfile));

        List<AiProfileResponse> result = aiProfileService.findAllProfiles(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        verify(aiProfileRepository).findByActive(true);
    }

    @Test
    void findAllProfiles_shouldReturnEmptyList_whenProfilesDoNotExist() {
        when(aiProfileRepository.findByActive(false)).thenReturn(List.of());

        assertThat(aiProfileService.findAllProfiles(false)).isEmpty();
    }

    @Test
    void getByLanguage_shouldThrowBadRequest_whenLanguageIsBlank() {
        assertThatThrownBy(() -> aiProfileService.getByLanguage("   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Язык AI-профиля не должен быть пустым.");
        verifyNoInteractions(aiProfileRepository);
    }

    @Test
    void getByLanguage_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.findFirstByLanguage("ru")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiProfileService.getByLanguage("  ru  "))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль с языком (ru) не найден.");
    }

    @Test
    void getByLanguage_shouldReturnProfile_whenProfileExists() {
        AiProfile profile = createSavedProfile(1L, "interview");
        when(aiProfileRepository.findFirstByLanguage("ru")).thenReturn(Optional.of(profile));

        AiProfileResponse result = aiProfileService.getByLanguage("  ru  ");

        assertAiProfileResponse(result, profile);
        verify(aiProfileRepository).findFirstByLanguage("ru");
    }

    private void assertInvalidProfile(Consumer<AiProfile> modification, String expectedMessage) {
        AiProfile profile = createAiProfile();
        modification.accept(profile);

        assertThatThrownBy(() -> aiProfileService.addProfile(profile))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(aiProfileRepository);
    }

    private AiProfile createAiProfile() {
        return new AiProfile(
                "interview",
                "Профиль для технического интервью",
                "Задавай вопросы по Java Backend",
                "mock-ai",
                "ru",
                "detailed",
                "medium",
                "detailed",
                false,
                false,
                0.70,
                1000
        );
    }

    private AiProfile createSavedProfile(Long id, String mode) {
        AiProfile profile = createAiProfile();
        profile.setId(id);
        profile.setMode(mode);
        return profile;
    }

    private void assertAiProfileResponse(AiProfileResponse response, AiProfile expected) {
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(expected.getId());
        assertThat(response.getMode()).isEqualTo(expected.getMode());
        assertThat(response.getDescriptionMode()).isEqualTo(expected.getDescriptionMode());
        assertThat(response.getInstructionMode()).isEqualTo(expected.getInstructionMode());
        assertThat(response.getModelName()).isEqualTo(expected.getModelName());
        assertThat(response.getLanguage()).isEqualTo(expected.getLanguage());
        assertThat(response.getAnswerStyle()).isEqualTo(expected.getAnswerStyle());
        assertThat(response.getDifficulty()).isEqualTo(expected.getDifficulty());
        assertThat(response.getFeedbackMode()).isEqualTo(expected.getFeedbackMode());
        assertThat(response.getHintMode()).isEqualTo(expected.getHintMode());
        assertThat(response.getActive()).isEqualTo(expected.getActive());
        assertThat(response.getTemperature()).isEqualTo(expected.getTemperature());
        assertThat(response.getMaxTokens()).isEqualTo(expected.getMaxTokens());
    }
}
