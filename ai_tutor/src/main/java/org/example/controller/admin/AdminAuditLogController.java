package org.example.controller.admin;

import org.example.dto.response.admin.audit.AdminRoleChangeAuditResponse;
import org.example.service.AdminRoleChangeAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/role-change-audit")
public class AdminAuditLogController {

    private final AdminRoleChangeAuditService adminRoleChangeAuditService;

    public AdminAuditLogController(AdminRoleChangeAuditService adminRoleChangeAuditService) {
        this.adminRoleChangeAuditService = adminRoleChangeAuditService;
    }

    @GetMapping
    public ResponseEntity<List<AdminRoleChangeAuditResponse>> getAuditLogs(
            @RequestParam(required = false) Long actorUserId,
            @RequestParam(required = false) Long targetUserId,
            @RequestParam(required = false) String actorUsername,
            @RequestParam(required = false) String targetUsername
    ) {
        List<AdminRoleChangeAuditResponse> auditLogs = adminRoleChangeAuditService.getAuditLog(actorUserId, targetUserId, actorUsername, targetUsername);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(auditLogs);
    }
}
