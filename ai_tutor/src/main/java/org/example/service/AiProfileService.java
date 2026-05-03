package org.example.service;

import org.example.model.AiProfile;

import java.util.List;

public interface AiProfileService {

    AiProfile addAiProfile(String mode,
                           String descriptionMode,
                           String instructionMode,
                           String language,
                           String answerStyle,
                           Boolean hintMode,
                           Boolean active);

    AiProfile updateAiProfile(Long id,
                              String mode,
                              String descriptionMode,
                              String instructionMode,
                              String language,
                              String answerStyle,
                              Boolean hintMode,
                              Boolean active);

    void deleteById(Long id);

    AiProfile getById(Long id);

    AiProfile getByMode(String mode);

    List<AiProfile> getAllProfiles();

    List<AiProfile> getActiveProfiles();

    AiProfile activateProfile(Long id);

    AiProfile deactivateProfile(Long id);
}