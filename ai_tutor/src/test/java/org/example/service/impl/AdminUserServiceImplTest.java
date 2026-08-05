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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRoleChangeAuditRepository adminRoleChangeAuditRepository;

    private AdminUserServiceImpl adminUserService;

    @BeforeEach
    void setUp() {
        adminUserService = new AdminUserServiceImpl(
                userRepository,
                new UserMapper(),
                adminRoleChangeAuditRepository
        );
    }

    @Test
    void updateUserRole_shouldThrowBadRequest_whenRoleIsNull() {
        assertThatThrownBy(() -> adminUserService.updateUserRole(1L, 2L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Роль пользователя должна быть указана.");

        verifyNoInteractions(userRepository, adminRoleChangeAuditRepository);
    }

    @Test
    void updateUserRole_shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.updateUserRole(1L, 2L, UserRole.ADMIN))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id=2 не найден.");

        verify(userRepository).findById(2L);
        verifyNoInteractions(adminRoleChangeAuditRepository);
    }

    @Test
    void updateUserRole_shouldChangeRoleAndSaveAudit_whenRoleIsDifferent() {
        User user = createUser(2L, "Yakov", UserRole.USER);
        User actorUser = createUser(1L, "Admin", UserRole.ADMIN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(actorUser));

        UserResponse response = adminUserService.updateUserRole(1L, 2L, UserRole.ADMIN);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);

        ArgumentCaptor<AdminRoleChangeAudit> auditCaptor =
                ArgumentCaptor.forClass(AdminRoleChangeAudit.class);
        verify(adminRoleChangeAuditRepository).save(auditCaptor.capture());

        AdminRoleChangeAudit audit = auditCaptor.getValue();
        assertThat(audit.getActorUserId()).isEqualTo(1L);
        assertThat(audit.getActorUsername()).isEqualTo("Admin");
        assertThat(audit.getActorEmail()).isEqualTo("admin@example.com");
        assertThat(audit.getTargetUserId()).isEqualTo(2L);
        assertThat(audit.getTargetUsername()).isEqualTo("Yakov");
        assertThat(audit.getTargetEmail()).isEqualTo("yakov@example.com");
        assertThat(audit.getOldRole()).isEqualTo(UserRole.USER);
        assertThat(audit.getNewRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void updateUserRole_shouldNotSaveAudit_whenRoleIsSame() {
        User user = createUser(2L, "Yakov", UserRole.ADMIN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        UserResponse response = adminUserService.updateUserRole(1L, 2L, UserRole.ADMIN);

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);

        verify(userRepository).findById(2L);
        verifyNoMoreInteractions(adminRoleChangeAuditRepository);
    }

    private User createUser(Long id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username.toLowerCase() + "@example.com");
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }
}
