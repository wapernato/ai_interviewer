package org.example.repository;

import org.example.model.AiProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiProfileRepository extends JpaRepository<AiProfile, Long> {
    Optional<AiProfile> findByMode(String mode);

    boolean existsByMode(String mode);

    Optional<AiProfile> findFirstByActiveTrue();

    List<AiProfile> findByActive(boolean active);

    AiProfile findByLanguage(String language);
}
