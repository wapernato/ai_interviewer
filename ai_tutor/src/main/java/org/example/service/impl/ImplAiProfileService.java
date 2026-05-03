package org.example.service.impl;

import org.example.DAO.AiProfileDAO;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;

import java.util.List;

public class ImplAiProfileService implements AiProfileService {

    private final AiProfileDAO aiProfileDAO;

    public ImplAiProfileService(AiProfileDAO aiProfileDAO) {
        this.aiProfileDAO = aiProfileDAO;
    }

    @Override
    public AiProfile addAiProfile(String mode,
                                  String descriptionMode,
                                  String instructionMode,
                                  String language,
                                  String answerStyle,
                                  Boolean hintMode,
                                  Boolean active) {

        validateMode(mode);
        validateInstructionMode(instructionMode);

        String preparedMode = mode.trim();

        AiProfile existingProfile = aiProfileDAO.findByMode(preparedMode);

        if (existingProfile != null) {
            throw new RuntimeException("AI-профиль с таким режимом уже существует.");
        }

        AiProfile aiProfile = new AiProfile();
        aiProfile.setMode(preparedMode);
        aiProfile.setDescriptionMode(normalizeNullableText(descriptionMode));
        aiProfile.setInstructionMode(instructionMode.trim());
        aiProfile.setLanguage(normalizeLanguage(language));
        aiProfile.setAnswerStyle(normalizeNullableText(answerStyle));
        aiProfile.setHintMode(normalizeBoolean(hintMode));
        aiProfile.setActive(normalizeBoolean(active));

        return aiProfileDAO.save(aiProfile);
    }

    @Override
    public AiProfile updateAiProfile(Long id,
                                     String mode,
                                     String descriptionMode,
                                     String instructionMode,
                                     String language,
                                     String answerStyle,
                                     Boolean hintMode,
                                     Boolean active) {

        validateId(id);
        validateMode(mode);
        validateInstructionMode(instructionMode);

        AiProfile existingProfile = aiProfileDAO.findById(id);

        if (existingProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        String preparedMode = mode.trim();

        AiProfile profileWithSameMode = aiProfileDAO.findByMode(preparedMode);

        if (profileWithSameMode != null && !profileWithSameMode.getId().equals(id)) {
            throw new RuntimeException("AI-профиль с таким режимом уже существует.");
        }

        existingProfile.setMode(preparedMode);
        existingProfile.setDescriptionMode(normalizeNullableText(descriptionMode));
        existingProfile.setInstructionMode(instructionMode.trim());
        existingProfile.setLanguage(normalizeLanguage(language));
        existingProfile.setAnswerStyle(normalizeNullableText(answerStyle));
        existingProfile.setHintMode(normalizeBoolean(hintMode));
        existingProfile.setActive(normalizeBoolean(active));

        return aiProfileDAO.update(existingProfile);
    }

    @Override
    public void deleteById(Long id) {
        validateId(id);

        AiProfile existingProfile = aiProfileDAO.findById(id);

        if (existingProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        aiProfileDAO.deleteById(id);
    }

    @Override
    public AiProfile getById(Long id) {
        validateId(id);

        AiProfile aiProfile = aiProfileDAO.findById(id);

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        return aiProfile;
    }

    @Override
    public AiProfile getByMode(String mode) {
        validateMode(mode);

        AiProfile aiProfile = aiProfileDAO.findByMode(mode.trim());

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким режимом не найден.");
        }

        return aiProfile;
    }

    @Override
    public List<AiProfile> getAllProfiles() {
        return aiProfileDAO.findAll();
    }

    @Override
    public List<AiProfile> getActiveProfiles() {
        return aiProfileDAO.findActiveProfiles();
    }

    @Override
    public AiProfile activateProfile(Long id) {
        validateId(id);

        AiProfile aiProfile = aiProfileDAO.findById(id);

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        aiProfile.setActive(true);

        return aiProfileDAO.update(aiProfile);
    }

    @Override
    public AiProfile deactivateProfile(Long id) {
        validateId(id);

        AiProfile aiProfile = aiProfileDAO.findById(id);

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        aiProfile.setActive(false);

        return aiProfileDAO.update(aiProfile);
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new RuntimeException("Id AI-профиля не может быть null.");
        }
    }

    private void validateMode(String mode) {
        if (mode == null || mode.isBlank()) {
            throw new RuntimeException("Режим AI-профиля не может быть пустым.");
        }

        if (mode.trim().length() < 2 || mode.trim().length() > 100) {
            throw new RuntimeException("Режим AI-профиля должен быть от 2 до 100 символов.");
        }
    }

    private void validateInstructionMode(String instructionMode) {
        if (instructionMode == null || instructionMode.isBlank()) {
            throw new RuntimeException("Инструкция AI-профиля не может быть пустой.");
        }
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "ru";
        }

        return language.trim();
    }

    private Boolean normalizeBoolean(Boolean value) {
        if (value == null) {
            return false;
        }

        return value;
    }

    private String normalizeNullableText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text.trim();
    }
}