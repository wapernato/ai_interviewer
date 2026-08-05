package org.example.mapper;

import org.example.dto.response.admin.audit.AdminRoleChangeAuditResponse;
import org.example.model.AdminRoleChangeAudit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminRoleChangeAuditMapper {

    public AdminRoleChangeAuditResponse toResponse(AdminRoleChangeAudit adminRoleChangeAudit) {
        if (adminRoleChangeAudit == null) {
            return null;
        }

        AdminRoleChangeAuditResponse response = new AdminRoleChangeAuditResponse();

        response.setId(adminRoleChangeAudit.getId());
        response.setActorUserId(adminRoleChangeAudit.getActorUserId());
        response.setActorUsername(adminRoleChangeAudit.getActorUsername());
        response.setActorEmail(adminRoleChangeAudit.getActorEmail());
        response.setTargetUserId(adminRoleChangeAudit.getTargetUserId());
        response.setTargetUsername(adminRoleChangeAudit.getTargetUsername());
        response.setTargetEmail(adminRoleChangeAudit.getTargetEmail());
        response.setOldRole(adminRoleChangeAudit.getOldRole());
        response.setNewRole(adminRoleChangeAudit.getNewRole());
        response.setChangedAt(adminRoleChangeAudit.getChangedAt());

        return response;
    }

    public List<AdminRoleChangeAuditResponse> toResponseList(List<AdminRoleChangeAudit> auditLogs) {
        if (auditLogs == null) {
            return List.of();
        }

        return auditLogs.stream()
                .map(this::toResponse)
                .toList();
    }
}
