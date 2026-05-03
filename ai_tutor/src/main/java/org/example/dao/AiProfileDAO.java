package org.example.dao;

import org.example.model.AiProfile;

import java.util.List;

public interface AiProfileDAO {

    AiProfile save(AiProfile aiProfile);
    AiProfile update(AiProfile aiProfile);
    AiProfile findById(Long id);
    AiProfile findByMode(String mode);
    List<AiProfile> findAll();
    List<AiProfile> findActiveProfiles();
    void deleteById(Long id);
}