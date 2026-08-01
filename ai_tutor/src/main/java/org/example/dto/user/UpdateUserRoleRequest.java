package org.example.dto.user;

import jakarta.validation.constraints.NotNull;
import org.example.model.UserRole;

public record UpdateUserRoleRequest (
        @NotNull(message = "Роль пользователя должна быть указана.")
        UserRole role
) {}
