package org.example.service;

import org.example.dto.user.UserHistoryItem;

import java.util.List;

public interface UserHistoryService {


    List<UserHistoryItem> findHistoryByUserId(Long userId);
}
