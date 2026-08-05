package org.example.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.dto.aiprofile.CreateAiProfileRequest;
import org.example.dto.response.aiprofile.AiProfileResponse;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ai-profiles")
public class AdminAiProfileController {

    private final AiProfileService aiProfileService;

    public AdminAiProfileController(AiProfileService aiProfileService) {
        this.aiProfileService = aiProfileService;
    }

    @PostMapping
    public ResponseEntity<AiProfileResponse> addNewProfile(@Valid @RequestBody CreateAiProfileRequest request) {
        AiProfile aiProfile = new AiProfile();

        aiProfile.setMode(request.getMode());
        aiProfile.setDescriptionMode(request.getDescriptionMode());
        aiProfile.setInstructionMode(request.getInstructionMode());

        aiProfile.setModelName(request.getModelName());
        aiProfile.setLanguage(request.getLanguage());
        aiProfile.setAnswerStyle(request.getAnswerStyle());

        aiProfile.setDifficulty(request.getDifficulty());
        aiProfile.setFeedbackMode(request.getFeedbackMode());

        aiProfile.setHintMode(request.getHintMode());
        aiProfile.setActive(request.getActive());

        aiProfile.setTemperature(request.getTemperature());
        aiProfile.setMaxTokens(request.getMaxTokens());

        AiProfileResponse savedProfile = aiProfileService.addProfile(aiProfile);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByAiProfileId(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id){
        aiProfileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AiProfileResponse> updateAiProfile(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id,@Valid @RequestBody CreateAiProfileRequest request) {

        AiProfile aiProfile = new AiProfile();
        aiProfile.setId(id);

        aiProfile.setMode(request.getMode());
        aiProfile.setDescriptionMode(request.getDescriptionMode());
        aiProfile.setInstructionMode(request.getInstructionMode());

        aiProfile.setModelName(request.getModelName());
        aiProfile.setLanguage(request.getLanguage());
        aiProfile.setAnswerStyle(request.getAnswerStyle());

        aiProfile.setDifficulty(request.getDifficulty());
        aiProfile.setFeedbackMode(request.getFeedbackMode());

        aiProfile.setHintMode(request.getHintMode());
        aiProfile.setActive(request.getActive());

        aiProfile.setTemperature(request.getTemperature());
        aiProfile.setMaxTokens(request.getMaxTokens());

        AiProfileResponse updatedProfile = aiProfileService.updateProfile(aiProfile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedProfile);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AiProfileResponse> activeAiProfile(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id){
        AiProfileResponse aiProfile = aiProfileService.activateProfile(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }

}
