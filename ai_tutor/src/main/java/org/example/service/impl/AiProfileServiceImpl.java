package org.example.service.impl;

import org.example.dto.response.AiProfileResponse;
import org.example.exception.AiProfileAlreadyExistsException;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.AiProfileMapper;
import org.example.model.AiProfile;
import org.example.repository.AiProfileRepository;
import org.example.service.AiProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AiProfileServiceImpl implements AiProfileService {

    private final AiProfileRepository aiProfileRepository;
    private final AiProfileMapper aiProfileMapper;

    public AiProfileServiceImpl(AiProfileRepository aiProfileRepository,
                                AiProfileMapper aiProfileMapper
    ) {
        this.aiProfileRepository = aiProfileRepository;
        this.aiProfileMapper = aiProfileMapper;
    }


    private void deactivateAll() {
        List<AiProfile> profiles = aiProfileRepository.findAll();

        for (AiProfile profile : profiles) {
            profile.setActive(false);
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Некорректный id.");
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
            aiProfile.setDifficulty("medium");
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
            throw new BadRequestException("Mode AI-профиля не должен быть пустым.");
        }

        if (aiProfile.getMode().length() < 2 || aiProfile.getMode().length() > 100) {
            throw new BadRequestException("Mode AI-профиля должен быть от 2 до 100 символов.");
        }

        if (aiProfile.getInstructionMode() == null || aiProfile.getInstructionMode().isBlank()) {
            throw new BadRequestException("Инструкция AI-профиля не должна быть пустой.");
        }

        if (aiProfile.getInstructionMode().length() < 10) {
            throw new BadRequestException("Инструкция AI-профиля слишком короткая.");
        }

        if (aiProfile.getModelName().length() > 100) {
            throw new BadRequestException("Название модели не должно быть длиннее 100 символов.");
        }

        if (aiProfile.getLanguage().length() > 20) {
            throw new BadRequestException("Название языка не должно быть длиннее 20 символов.");
        }

        if (aiProfile.getAnswerStyle().length() > 50) {
            throw new BadRequestException("Стиль ответа не должен быть длиннее 50 символов.");
        }

        if (aiProfile.getDifficulty().length() > 30) {
            throw new BadRequestException("Сложность не должна быть длиннее 30 символов.");
        }

        if (aiProfile.getFeedbackMode().length() > 50) {
            throw new BadRequestException("Режим обратной связи не должен быть длиннее 50 символов.");
        }

        if (aiProfile.getTemperature() < 0 || aiProfile.getTemperature() > 2) {
            throw new BadRequestException("Temperature должна быть в диапазоне от 0 до 2.");
        }

        if (aiProfile.getMaxTokens() <= 0) {
            throw new BadRequestException("Max tokens должен быть больше 0.");
        }

        if (aiProfile.getMaxTokens() > 4000) {
            throw new BadRequestException("Max tokens пока не должен быть больше 4000.");
        }
    }

    @Transactional
    @Override
    public AiProfileResponse addProfile(AiProfile aiProfile) {
        if (aiProfile == null) {
            throw new BadRequestException("AI-профиль не должен быть null.");
        }

        normalizeProfile(aiProfile);
        validateProfile(aiProfile);

        Optional<AiProfile> existingProfile = aiProfileRepository.findByMode(aiProfile.getMode());

        if (existingProfile.isPresent()) {
            throw new AiProfileAlreadyExistsException("AI-профиль с таким mode уже существует.");
        }

        if (Boolean.TRUE.equals(aiProfile.getActive())) {
            deactivateAll();
        }

        AiProfile savedAiProfile = aiProfileRepository.save(aiProfile);

        return aiProfileMapper.toResponse(savedAiProfile);
    }

    @Transactional(readOnly = true)
    @Override
    public AiProfileResponse getById(Long id) {
        validateId(id);

        AiProfile savedAiProfile = aiProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AI-профиль по id=" + id + " не найден."));

        return aiProfileMapper.toResponse(savedAiProfile);
    }

    @Transactional(readOnly = true)
    @Override
    public AiProfileResponse getByMode(String mode) {
        if (mode == null || mode.isBlank()) {
            throw new BadRequestException("Mode AI-профиля не должен быть пустым.");
        }

        mode = mode.trim();

        String finalMode = mode;
        AiProfile savedAiProfile = aiProfileRepository.findByMode(mode)
                .orElseThrow(() -> new NotFoundException("Мод с названием (" + finalMode + ") не найден."));

        return aiProfileMapper.toResponse(savedAiProfile);

    }

    @Transactional(readOnly = true)
    @Override
    public List<AiProfileResponse> getAllProfiles() {
        List<AiProfile> savedAiProfileList = aiProfileRepository.findAll();

        return aiProfileMapper.toResponseList(savedAiProfileList);
    }

    @Transactional
    @Override
    public AiProfileResponse updateProfile(AiProfile aiProfile) {
        if (aiProfile == null) {
            throw new BadRequestException("AI-профиль не должен быть null.");
        }

        validateId(aiProfile.getId());

        AiProfile oldProfile = aiProfileRepository.findById(aiProfile.getId())
                .orElseThrow(() -> new NotFoundException("AI-профиль с id=" + aiProfile.getId() +" не найден."));

        normalizeProfile(aiProfile);
        validateProfile(aiProfile);

        Optional<AiProfile> profileWithSameMode = aiProfileRepository.findByMode(aiProfile.getMode());


        if (profileWithSameMode.isPresent() && !profileWithSameMode.get().getId().equals(aiProfile.getId())) {
            throw new AiProfileAlreadyExistsException("AI-профиль с таким mode уже существует.");
        }

        if (Boolean.TRUE.equals(aiProfile.getActive())) {
            deactivateAll();
        }

        oldProfile.setMode(aiProfile.getMode());
        oldProfile.setDescriptionMode(aiProfile.getDescriptionMode());
        oldProfile.setInstructionMode(aiProfile.getInstructionMode());
        oldProfile.setModelName(aiProfile.getModelName());
        oldProfile.setLanguage(aiProfile.getLanguage());
        oldProfile.setAnswerStyle(aiProfile.getAnswerStyle());
        oldProfile.setDifficulty(aiProfile.getDifficulty());
        oldProfile.setFeedbackMode(aiProfile.getFeedbackMode());
        oldProfile.setHintMode(aiProfile.getHintMode());
        oldProfile.setActive(aiProfile.getActive());
        oldProfile.setTemperature(aiProfile.getTemperature());
        oldProfile.setMaxTokens(aiProfile.getMaxTokens());

        AiProfile savedAiProfile = aiProfileRepository.save(oldProfile);;

        return aiProfileMapper.toResponse(savedAiProfile);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        validateId(id);

        aiProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AI-профиль с таким id не найден."));

        aiProfileRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public AiProfileResponse getActiveProfile() {
        AiProfile savedAiProfile = aiProfileRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new NotFoundException("Активный AI-профиль не найден."));

        return aiProfileMapper.toResponse(savedAiProfile);
    }

    @Transactional
    @Override
    public AiProfileResponse activateProfile(Long id) {
        validateId(id);

        AiProfile aiProfile = aiProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("AI-профиль по id = " + id + " не найден."));

        deactivateAll();

        aiProfile.setActive(true);

        AiProfile savedAiProfile = aiProfileRepository.save(aiProfile);

        return aiProfileMapper.toResponse(savedAiProfile);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AiProfileResponse> findAllProfiles(boolean active) {

        List<AiProfile> savedAiProfileList = aiProfileRepository.findByActive(active);
        return aiProfileMapper.toResponseList(savedAiProfileList);
    }

    @Transactional(readOnly = true)
    @Override
    public AiProfileResponse getByLanguage(String language){
        if (language == null || language.isBlank()) {
            throw new BadRequestException("Язык AI-профиля не должен быть пустым.");
        }

        String normalizedLanguage = language.trim();

        AiProfile savedAiProfile = aiProfileRepository.findFirstByLanguage(normalizedLanguage)
                .orElseThrow(() -> new NotFoundException("AI-профиль с языком (" + normalizedLanguage + ") не найден."));

        return aiProfileMapper.toResponse(savedAiProfile);
    }
}
