package org.example.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "admin_role_change_audit")
public class AdminRoleChangeAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_user_id", nullable = false, updatable = false)
    private Long actorUserId;
    @Column(name = "actor_username", nullable = false, updatable = false, length = 100)
    private String actorUsername;
    @Column(name = "actor_email", nullable = false, updatable = false, length = 100)
    private String actorEmail;
    @Column(name = "target_user_id", nullable = false, updatable = false)
    private Long targetUserId;
    @Column(name = "target_username", nullable = false, updatable = false, length = 100)
    private String targetUsername;
    @Column(name = "target_email", nullable = false, updatable = false, length = 100)
    private String targetEmail;
    @Enumerated(EnumType.STRING)
    @Column(name = "old_role", nullable = false, updatable = false)
    private UserRole oldRole;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_role", nullable = false, updatable = false)
    private UserRole newRole;
    @Column(name = "changed_at", nullable = false, insertable = false, updatable = false)
    private Instant changedAt;

    protected AdminRoleChangeAudit() {}

    public AdminRoleChangeAudit(Long actorUserId,
                                String actorUsername,
                                String actorEmail,
                                Long targetUserId,
                                String targetUsername,
                                String targetEmail,
                                UserRole oldRole,
                                UserRole newRole
                                ) {
        this.actorUserId = actorUserId;
        this.actorUsername = actorUsername;
        this.actorEmail = actorEmail;
        this.targetUserId = targetUserId;
        this.targetUsername = targetUsername;
        this.targetEmail = targetEmail;
        this.oldRole = oldRole;
        this.newRole = newRole;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public UserRole getNewRole() {
        return newRole;
    }

    public UserRole getOldRole() {
        return oldRole;
    }

    public Long getId() {
        return id;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getTargetEmail() {
        return targetEmail;
    }
}
