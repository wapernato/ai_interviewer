package org.example.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.dto.aiprofile.CreateAiProfileRequest;
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
    public ResponseEntity<List<AiProfile>> getAllAiProfile(){
        List<AiProfile> aiProfiles = aiProfileService.getAllProfiles();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiProfile> getAiProfileById(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id){
        AiProfile aiProfile = aiProfileService.getById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }

    @PostMapping
    public ResponseEntity<AiProfile> addNewProfile(@Valid @RequestBody CreateAiProfileRequest request) {
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

        AiProfile savedProfile = aiProfileService.addProfile(aiProfile);
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
    public ResponseEntity<AiProfile> updateAiProfile(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id,@Valid @RequestBody CreateAiProfileRequest request) {

        AiProfile aiProfile = aiProfileService.getById(id);

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

        AiProfile updatedProfile = aiProfileService.updateProfile(aiProfile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedProfile);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AiProfile> activeAiProfile(@PathVariable @Positive(message = "ID AI-профиля должен быть положительным числом.") Long id){
        AiProfile aiProfile = aiProfileService.activateProfile(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }

    @GetMapping("/search")
    public ResponseEntity<AiProfile> findByMode(@NotBlank(message = "Мод должен быть указан.") @Size(min = 2, max = 100) @RequestParam String mode){
        AiProfile aiProfile = aiProfileService.getByMode(mode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<AiProfile>> findByActive(@RequestParam boolean active){
        List<AiProfile> aiProfiles = aiProfileService.findAllProfiles(active);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfiles);
    }

    @GetMapping("/language")
    public ResponseEntity<AiProfile> findByLanguage(@NotBlank(message = "Мод должен быть указан.") @RequestParam String language){
        AiProfile aiProfile = aiProfileService.getByLanguage(language);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aiProfile);
    }


}
