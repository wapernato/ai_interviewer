package org.example.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.dto.aiprofile.CreateAiProfileRequest;
import org.example.dto.response.AiProfileResponse;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/ai-profiles")
public class AiProfileController {

    private final AiProfileService aiProfileService;

    public AiProfileController(AiProfileService aiProfileService) { this.aiProfileService = aiProfileService; }

    @GetMapping
    public ResponseEntity<List<AiProfileResponse>> getAllAiProfile(){
        List<AiProfileResponse> aiProfiles = aiProfileService.getAllProfiles();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiProfileResponse> getAiProfileById(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id){
        AiProfileResponse aiProfile = aiProfileService.getById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
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

    @GetMapping("/search")
    public ResponseEntity<AiProfileResponse> findByMode(@NotBlank(message = "Мод должен быть указан.") @Size(min = 2, max = 100) @RequestParam String mode){
        AiProfileResponse aiProfile = aiProfileService.getByMode(mode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<AiProfileResponse>> findByActive(@RequestParam boolean active){
        List<AiProfileResponse> aiProfiles = aiProfileService.findAllProfiles(active);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfiles);
    }

    @GetMapping("/language")
    public ResponseEntity<AiProfileResponse> findByLanguage(@NotBlank(message = "Язык должен быть указан.") @RequestParam String language){
        AiProfileResponse aiProfile = aiProfileService.getByLanguage(language);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }


}
