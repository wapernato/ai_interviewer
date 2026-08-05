package org.example.mapper;

import org.example.dto.response.aiprofile.InternalAiProfileConfigResponse;
import org.example.model.AiProfile;
import org.springframework.stereotype.Component;

@Component
public class InternalAiProfileMapper {

    public InternalAiProfileConfigResponse toInternalConfigResponse(AiProfile profile){
        if(profile == null){
            return null;
        }

        return new InternalAiProfileConfigResponse(
                profile.getId(),
                profile.getMode(),
                profile.getInstructionMode(),
                profile.getModelName(),
                profile.getLanguage(),
                profile.getAnswerStyle(),
                profile.getDifficulty(),
                profile.getFeedbackMode(),
                Boolean.TRUE.equals(profile.getHintMode()),
                profile.getTemperature(),
                profile.getMaxTokens()
        );

    }
}
