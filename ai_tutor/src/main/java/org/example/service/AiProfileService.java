package org.example.service;

import org.example.dto.response.AiProfileResponse;
import org.example.model.AiProfile;

import java.util.List;

public interface AiProfileService {

    AiProfileResponse addProfile(AiProfile aiProfile);

    AiProfileResponse getById(Long id);

    AiProfileResponse getByMode(String mode);

    List<AiProfileResponse> getAllProfiles();

    AiProfileResponse updateProfile(AiProfile aiProfile);

    void deleteById(Long id);

    AiProfileResponse getActiveProfile();

    AiProfileResponse activateProfile(Long id);

    List<AiProfileResponse> findAllProfiles(boolean active);

    AiProfileResponse getByLanguage(String language);
}