package org.example.service;

import org.example.dto.response.InternalAiProfileConfigResponse;

public interface InternalAiProfile {
    InternalAiProfileConfigResponse getActiveProfileConfig(Long id);
}
