package org.example.service.impl;

import org.example.dao.AiProfileDAO;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;

import java.util.List;

public class ImplAiProfileService implements AiProfileService {

    private final AiProfileDAO aiProfileDAO;

    public ImplAiProfileService(AiProfileDAO aiProfileDAO) {
        this.aiProfileDAO = aiProfileDAO;
    }

    @Override
    public AiProfile addProfile(AiProfile aiProfile) {
        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль не должен быть null.");
        }

        normalizeProfile(aiProfile);
        validateProfile(aiProfile);

        AiProfile existingProfile = aiProfileDAO.findByMode(aiProfile.getMode());

        if (existingProfile != null) {
            throw new RuntimeException("AI-профиль с таким mode уже существует.");
        }

        if (Boolean.TRUE.equals(aiProfile.getActive())) {
            aiProfileDAO.deactivateAll();
        }

        return aiProfileDAO.save(aiProfile);
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
        if (mode == null || mode.isBlank()) {
            throw new RuntimeException("Mode AI-профиля не должен быть пустым.");
        }

        mode = mode.trim();

        AiProfile aiProfile = aiProfileDAO.findByMode(mode);

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким mode не найден.");
        }

        return aiProfile;
    }

    @Override
    public List<AiProfile> getAllProfiles() {
        return aiProfileDAO.findAll();
    }

    @Override
    public AiProfile updateProfile(AiProfile aiProfile) {
        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль не должен быть null.");
        }

        validateId(aiProfile.getId());

        AiProfile oldProfile = aiProfileDAO.findById(aiProfile.getId());

        if (oldProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        normalizeProfile(aiProfile);
        validateProfile(aiProfile);

        AiProfile profileWithSameMode = aiProfileDAO.findByMode(aiProfile.getMode());

        if (profileWithSameMode != null && !profileWithSameMode.getId().equals(aiProfile.getId())) {
            throw new RuntimeException("AI-профиль с таким mode уже существует.");
        }

        if (Boolean.TRUE.equals(aiProfile.getActive())) {
            aiProfileDAO.deactivateAll();
        }

        return aiProfileDAO.update(aiProfile);
    }

    @Override
    public void deleteById(Long id) {
        validateId(id);

        AiProfile aiProfile = aiProfileDAO.findById(id);

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        aiProfileDAO.deleteById(id);
    }

    @Override
    public AiProfile getActiveProfile() {
        AiProfile activeProfile = aiProfileDAO.findActive();

        if (activeProfile == null) {
            throw new RuntimeException("Активный AI-профиль не найден.");
        }

        return activeProfile;
    }

    @Override
    public AiProfile activateProfile(Long id) {
        validateId(id);

        AiProfile aiProfile = aiProfileDAO.findById(id);

        if (aiProfile == null) {
            throw new RuntimeException("AI-профиль с таким id не найден.");
        }

        aiProfileDAO.deactivateAll();

        AiProfile activatedProfile = aiProfileDAO.activateById(id);

        if (activatedProfile == null) {
            throw new RuntimeException("Не удалось активировать AI-профиль.");
        }

        return activatedProfile;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Некорректный id.");
        }
    }

    private void normalizeProfile(AiProfile aiProfile) {
        if (aiProfile.getMode() != null) {
            aiProfile.setMode(aiProfile.getMode().trim());
        }

        if (aiProfile.getDescriptionMode() != null) {
            aiProfile.setDescriptionMode(aiProfile.getDescriptionMode().trim());
        }

        if (aiProfile.getInstructionMode() != null) {
            aiProfile.setInstructionMode(aiProfile.getInstructionMode().trim());
        }

        if (aiProfile.getModelName() == null || aiProfile.getModelName().isBlank()) {
            aiProfile.setModelName("mock-ai");
        } else {
            aiProfile.setModelName(aiProfile.getModelName().trim());
        }

        if (aiProfile.getLanguage() == null || aiProfile.getLanguage().isBlank()) {
            aiProfile.setLanguage("ru");
        } else {
            aiProfile.setLanguage(aiProfile.getLanguage().trim());
        }

        if (aiProfile.getAnswerStyle() == null || aiProfile.getAnswerStyle().isBlank()) {
            aiProfile.setAnswerStyle("detailed");
        } else {
            aiProfile.setAnswerStyle(aiProfile.getAnswerStyle().trim());
        }

        if (aiProfile.getDifficulty() == null || aiProfile.getDifficulty().isBlank()) {
            aiProfile.setDifficulty("middle");
        } else {
            aiProfile.setDifficulty(aiProfile.getDifficulty().trim());
        }

        if (aiProfile.getFeedbackMode() == null || aiProfile.getFeedbackMode().isBlank()) {
            aiProfile.setFeedbackMode("detailed");
        } else {
            aiProfile.setFeedbackMode(aiProfile.getFeedbackMode().trim());
        }

        if (aiProfile.getHintMode() == null) {
            aiProfile.setHintMode(false);
        }

        if (aiProfile.getActive() == null) {
            aiProfile.setActive(false);
        }

        if (aiProfile.getTemperature() == null) {
            aiProfile.setTemperature(0.70);
        }

        if (aiProfile.getMaxTokens() == null) {
            aiProfile.setMaxTokens(1000);
        }
    }

    private void validateProfile(AiProfile aiProfile) {
        if (aiProfile.getMode() == null || aiProfile.getMode().isBlank()) {
            throw new RuntimeException("Mode AI-профиля не должен быть пустым.");
        }

        if (aiProfile.getMode().length() < 2 || aiProfile.getMode().length() > 100) {
            throw new RuntimeException("Mode AI-профиля должен быть от 2 до 100 символов.");
        }

        if (aiProfile.getInstructionMode() == null || aiProfile.getInstructionMode().isBlank()) {
            throw new RuntimeException("Инструкция AI-профиля не должна быть пустой.");
        }

        if (aiProfile.getInstructionMode().length() < 10) {
            throw new RuntimeException("Инструкция AI-профиля слишком короткая.");
        }

        if (aiProfile.getModelName().length() > 100) {
            throw new RuntimeException("Название модели не должно быть длиннее 100 символов.");
        }

        if (aiProfile.getLanguage().length() > 20) {
            throw new RuntimeException("Название языка не должно быть длиннее 20 символов.");
        }

        if (aiProfile.getAnswerStyle().length() > 50) {
            throw new RuntimeException("Стиль ответа не должен быть длиннее 50 символов.");
        }

        if (aiProfile.getDifficulty().length() > 30) {
            throw new RuntimeException("Сложность не должна быть длиннее 30 символов.");
        }

        if (aiProfile.getFeedbackMode().length() > 50) {
            throw new RuntimeException("Режим обратной связи не должен быть длиннее 50 символов.");
        }

        if (aiProfile.getTemperature() < 0 || aiProfile.getTemperature() > 2) {
            throw new RuntimeException("Temperature должна быть в диапазоне от 0 до 2.");
        }

        if (aiProfile.getMaxTokens() <= 0) {
            throw new RuntimeException("Max tokens должен быть больше 0.");
        }

        if (aiProfile.getMaxTokens() > 4000) {
            throw new RuntimeException("Max tokens пока не должен быть больше 4000.");
        }
    }
}