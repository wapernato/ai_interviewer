package org.example.repository;

import org.example.model.AdminRoleChangeAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRoleChangeAuditRepository extends JpaRepository<AdminRoleChangeAudit, Long> {
    List<AdminRoleChangeAudit> findByActorUserIdOrderByChangedAtDesc(Long actorUserId);

    List<AdminRoleChangeAudit> findByTargetUserIdOrderByChangedAtDesc(Long targetUserId);
}
