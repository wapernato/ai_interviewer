package org.example.service.impl;

import org.example.dto.response.admin.audit.AdminRoleChangeAuditResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.AdminRoleChangeAuditMapper;
import org.example.model.AdminRoleChangeAudit;
import org.example.model.User;
import org.example.repository.AdminRoleChangeAuditRepository;
import org.example.repository.UserRepository;
import org.example.service.AdminRoleChangeAuditService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminRoleChangeAuditServiceImpl implements AdminRoleChangeAuditService {

    private final AdminRoleChangeAuditRepository adminRoleChangeAuditRepository;
    private final AdminRoleChangeAuditMapper adminRoleChangeAuditMapper;
    private final UserRepository userRepository;

    public AdminRoleChangeAuditServiceImpl(AdminRoleChangeAuditRepository adminRoleChangeAuditRepository,
                                           AdminRoleChangeAuditMapper adminRoleChangeAuditMapper,
                                           UserRepository userRepository){
        this.adminRoleChangeAuditRepository = adminRoleChangeAuditRepository;
        this.adminRoleChangeAuditMapper = adminRoleChangeAuditMapper;
        this.userRepository = userRepository;
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }

        return username.trim();
    }

    private int countFilters(Long actorUserId, Long targetUserId, String actorUsername, String targetUsername) {
        int filtersCount = 0;

        if (actorUserId != null) {
            filtersCount++;
        }
        if (targetUserId != null) {
            filtersCount++;
        }
        if (actorUsername != null) {
            filtersCount++;
        }
        if (targetUsername != null) {
            filtersCount++;
        }

        return filtersCount;
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь с username=" + username + " не найден."));
    }

    private List<AdminRoleChangeAuditResponse> toResponseList(List<AdminRoleChangeAudit> auditLogs) {
        return adminRoleChangeAuditMapper.toResponseList(auditLogs);
    }

    @Override
    public List<AdminRoleChangeAuditResponse> getAuditLog(Long actorUserId, Long targetUserId, String actorUsername, String targetUsername) {
        String normalizedActorUsername = normalizeUsername(actorUsername);
        String normalizedTargetUsername = normalizeUsername(targetUsername);

        if (countFilters(actorUserId, targetUserId, normalizedActorUsername, normalizedTargetUsername) > 1) {
            throw new BadRequestException("Нельзя одновременно фильтровать журнал по нескольким параметрам.");
        }

        if (actorUserId != null) {
            return toResponseList(adminRoleChangeAuditRepository.findByActorUserIdOrderByChangedAtDesc(actorUserId));
        }

        if (targetUserId != null) {
            return toResponseList(adminRoleChangeAuditRepository.findByTargetUserIdOrderByChangedAtDesc(targetUserId));
        }

        if (normalizedActorUsername != null) {
            User actorUser = findUserByUsername(normalizedActorUsername);
            return toResponseList(adminRoleChangeAuditRepository.findByActorUserIdOrderByChangedAtDesc(actorUser.getId()));
        }

        if (normalizedTargetUsername != null) {
            User targetUser = findUserByUsername(normalizedTargetUsername);
            return toResponseList(adminRoleChangeAuditRepository.findByTargetUserIdOrderByChangedAtDesc(targetUser.getId()));
        }

        return toResponseList(adminRoleChangeAuditRepository.findAll(Sort.by(Sort.Direction.DESC, "changedAt")));
    }


}
