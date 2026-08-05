package org.example.service.impl;

import org.example.dto.response.aiprofile.InternalAiProfileConfigResponse;
import org.example.exception.AiProfileUnavailableException;
import org.example.exception.NotFoundException;
import org.example.mapper.InternalAiProfileMapper;
import org.example.model.AiProfile;
import org.example.repository.AiProfileRepository;
import org.example.service.InternalAiProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InternalAiProfileImpl implements InternalAiProfile {

    private final AiProfileRepository aiProfileRepository;
    private final InternalAiProfileMapper internalAiProfileMapper;

    public InternalAiProfileImpl(AiProfileRepository aiProfileRepository,
                                 InternalAiProfileMapper internalAiProfileMapper){
        this.aiProfileRepository = aiProfileRepository;
        this.internalAiProfileMapper = internalAiProfileMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public InternalAiProfileConfigResponse getActiveProfileConfig(Long id){
        AiProfile aiProfile = aiProfileRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("AI-профиль с id=" + id + " не найден."));

        if (!Boolean.TRUE.equals(aiProfile.getActive())) {
            throw new AiProfileUnavailableException(
                    "AI-профиль с id=" + id + " недоступен."
            );
        }

        return internalAiProfileMapper.toInternalConfigResponse(aiProfile);
    }
}
