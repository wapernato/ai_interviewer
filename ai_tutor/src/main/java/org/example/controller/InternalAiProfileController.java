package org.example.controller;

import jakarta.validation.constraints.Positive;
import org.example.dto.response.InternalAiProfileConfigResponse;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;
import org.example.service.InternalAiProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/ai-profiles")
public class InternalAiProfileController {

    private final InternalAiProfile internalAiProfile;

    public InternalAiProfileController(InternalAiProfile internalAiProfile) {
        this.internalAiProfile = internalAiProfile;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalAiProfileConfigResponse> getProfileConfig(
            @PathVariable
            @Positive(message = "ID AI-профиля должен быть положительным.")
            Long id){
        InternalAiProfileConfigResponse internalAiProfileConfigResponse = internalAiProfile.getActiveProfileConfig(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(internalAiProfileConfigResponse);
    }


}
