package org.example.service;

import org.example.dto.response.admin.audit.AdminRoleChangeAuditResponse;

import java.util.List;

public interface AdminRoleChangeAuditService {
    List<AdminRoleChangeAuditResponse> getAuditLog(Long actorUserId, Long targetUserId, String actorUsername, String targetUsername);
}
