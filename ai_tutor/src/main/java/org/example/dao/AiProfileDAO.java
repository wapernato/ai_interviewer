package org.example.dao;

import org.example.model.AiProfile;

import java.util.List;

public interface AiProfileDAO {

    AiProfile save(AiProfile aiProfile);

    AiProfile findById(Long id);

    AiProfile findByMode(String mode);

    List<AiProfile> findAll();

    AiProfile update(AiProfile aiProfile);

    void deleteById(Long id);

    AiProfile findActive();

    void deactivateAll();

    AiProfile activateById(Long id);
}