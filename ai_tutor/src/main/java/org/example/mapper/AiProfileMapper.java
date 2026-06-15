package org.example.mapper;

import org.example.dto.response.AiProfileResponse;
import org.example.model.AiProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiProfileMapper {

    public AiProfileResponse toResponse(AiProfile aiProfile){

        if(aiProfile == null){
            return null;
        }

        AiProfileResponse aiProfileResponse = new AiProfileResponse();

        aiProfileResponse.setId(aiProfile.getId());
        aiProfileResponse.setMode(aiProfile.getMode());
        aiProfileResponse.setDescriptionMode(aiProfile.getDescriptionMode());
        aiProfileResponse.setInstructionMode(aiProfile.getInstructionMode());
        aiProfileResponse.setModelName(aiProfile.getModelName());
        aiProfileResponse.setLanguage(aiProfile.getLanguage());
        aiProfileResponse.setAnswerStyle(aiProfile.getAnswerStyle());
        aiProfileResponse.setDifficulty(aiProfile.getDifficulty());
        aiProfileResponse.setFeedbackMode(aiProfile.getFeedbackMode());
        aiProfileResponse.setHintMode(aiProfile.getHintMode());
        aiProfileResponse.setActive(aiProfile.getActive());
        aiProfileResponse.setTemperature(aiProfile.getTemperature());
        aiProfileResponse.setMaxTokens(aiProfile.getMaxTokens());

        return aiProfileResponse;
    }

    public List<AiProfileResponse> toResponseList(List<AiProfile> aiProfiles) {
        if(aiProfiles == null){
            return List.of();
        }

        return aiProfiles.stream()
                .map(this::toResponse)
                .toList();
    }
}
