package org.example.service;

import org.example.model.AiProfile;

import java.util.List;

public interface AiProfileService {

    AiProfile addProfile(AiProfile aiProfile);

    AiProfile getById(Long id);

    AiProfile getByMode(String mode);

    List<AiProfile> getAllProfiles();

    AiProfile updateProfile(AiProfile aiProfile);

    void deleteById(Long id);

    AiProfile getActiveProfile();

    AiProfile activateProfile(Long id);
}