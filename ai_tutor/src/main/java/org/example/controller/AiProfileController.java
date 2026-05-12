package org.example.controller;


import org.example.dto.aiprofile.CreateAiProfileRequest;
import org.example.model.AiProfile;
import org.example.service.AiProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aiProfile")
public class AiProfileController {

    private final AiProfileService aiProfileService;

    public AiProfileController(AiProfileService aiProfileService) { this.aiProfileService = aiProfileService; }

    @GetMapping
    public List<AiProfile> getAllAiProfile(){
        return aiProfileService.getAllProfiles();
    }

    @GetMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByAiProfileId(@PathVariable Long id){
        aiProfileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public AiProfile updateAiProfile(@PathVariable Long id,@RequestBody CreateAiProfileRequest request){

        AiProfile aiProfile = aiProfileService.getById(id);

        aiProfile.setMode(request.getMode());
        aiProfile.setDescriptionMode(request.getDescriptionMode());
        aiProfile.setInstructionMode(request.getInstructionMode());
        aiProfile.setLanguage(request.getLanguage());
        aiProfile.setAnswerStyle(request.getAnswerStyle());
        aiProfile.setHintMode(request.getHintMode());
        aiProfile.setActive(request.getActive());

        return aiProfileService.updateProfile(aiProfile);
    }

}
