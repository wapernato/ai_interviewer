package org.example.service;

import org.example.dto.response.aiprofile.InternalAiProfileConfigResponse;

public interface InternalAiProfile {
    InternalAiProfileConfigResponse getActiveProfileConfig(Long id);
}
