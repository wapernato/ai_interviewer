package org.example.controller;


import org.example.dto.Aiprofile.CreateAiProfileRequest;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aiProfile")
public class AiProfileController {

    private final AiProfileService aiProfileService;

    public AiProfileController(AiProfileService aiProfileService) { this.aiProfileService = aiProfileService; }

    @PutMapping
    public List<AiProfile> getAllAiProfile(){
        return aiProfileService.getAllProfiles();
    }

    @PutMapping("/{id}")
    public AiProfile getAiProfileById(@PathVariable Long id){
        return aiProfileService.getById(id);
    }

    @PostMapping
    public AiProfile addNewProfile(@RequestBody CreateAiProfileRequest request) {
        AiProfile aiProfile = new AiProfile();

        aiProfile.setMode(request.getMode());
        aiProfile.setDescriptionMode(request.getDescriptionMode());
        aiProfile.setInstructionMode(request.getInstructionMode());
        aiProfile.setLanguage(request.getLanguage());
        aiProfile.setAnswerStyle(request.getAnswerStyle());
        aiProfile.setHintMode(request.getHintMode());
        aiProfile.setActive(request.getActive());

        return aiProfileService.addProfile(aiProfile);
    }

    
}
