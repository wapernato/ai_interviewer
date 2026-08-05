package org.example.service;

import org.example.dto.response.user.UserResponse;
import org.example.model.UserRole;

public interface AdminUserService {
    UserResponse updateUserRole(Long id, UserRole role);
}
