package org.example.service.impl;

import org.example.dto.response.user.UserResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.AdminRoleChangeAudit;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.AdminRoleChangeAuditRepository;
import org.example.repository.UserRepository;
import org.example.service.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdminRoleChangeAuditRepository adminRoleChangeAuditRepository;
    private final UserMapper userMapper;

    public AdminUserServiceImpl(UserRepository userRepository,
                                UserMapper userMapper,
                                AdminRoleChangeAuditRepository adminRoleChangeAuditRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.adminRoleChangeAuditRepository = adminRoleChangeAuditRepository;
    }

    @Transactional
    @Override
    public UserResponse updateUserRole(Long actorUserId, Long id, UserRole role) {
        if (role == null) {
            throw new BadRequestException("Роль пользователя должна быть указана.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));

        if (!user.getRole().equals(role)) {
            User actorUser = userRepository.findById(actorUserId)
                    .orElseThrow(() -> new NotFoundException("Админ с id=" + actorUserId + " не найден."));

            adminRoleChangeAuditRepository.save(new AdminRoleChangeAudit(
                    actorUserId,
                    actorUser.getUsername(),
                    actorUser.getEmail(),
                    id,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    role
            ));
            user.setRole(role);
        }

        return userMapper.toResponse(user);
    }
}
