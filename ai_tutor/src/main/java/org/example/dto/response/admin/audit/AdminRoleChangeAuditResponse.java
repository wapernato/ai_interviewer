package org.example.dto.response.admin.audit;

import org.example.model.UserRole;

import java.time.Instant;

public class AdminRoleChangeAuditResponse {
    private Long id;

    private Long actorUserId;
    private String actorUsername;
    private String actorEmail;

    private Long targetUserId;
    private String targetUsername;
    private String targetEmail;

    private UserRole oldRole;
    private UserRole newRole;

    private Instant changedAt;

    public AdminRoleChangeAuditResponse() {
    }

    public AdminRoleChangeAuditResponse(Long id,
                                        Long actorUserId,
                                        String actorUsername,
                                        String actorEmail,
                                        Long targetUserId,
                                        String targetUsername,
                                        String targetEmail,
                                        UserRole oldRole,
                                        UserRole newRole,
                                        Instant changedAt) {
        this.id = id;
        this.actorUserId = actorUserId;
        this.actorUsername = actorUsername;
        this.actorEmail = actorEmail;
        this.targetUserId = targetUserId;
        this.targetUsername = targetUsername;
        this.targetEmail = targetEmail;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public void setActorUsername(String actorUsername) {
        this.actorUsername = actorUsername;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public UserRole getOldRole() {
        return oldRole;
    }

    public void setOldRole(UserRole oldRole) {
        this.oldRole = oldRole;
    }

    public UserRole getNewRole() {
        return newRole;
    }

    public void setNewRole(UserRole newRole) {
        this.newRole = newRole;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

}
