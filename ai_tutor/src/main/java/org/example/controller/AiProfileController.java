package org.example.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.dto.response.AiProfileResponse;
import org.example.service.AiProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
