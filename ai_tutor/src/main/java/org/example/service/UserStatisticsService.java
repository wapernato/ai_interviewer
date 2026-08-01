package org.example.service;

import org.example.dto.user.UserStatisticsResponse;

public interface UserStatisticsService {
    UserStatisticsResponse getUserStatistics(Long id);
}
