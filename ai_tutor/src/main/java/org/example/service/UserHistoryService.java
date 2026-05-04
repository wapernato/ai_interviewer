package org.example.service;

import org.example.dto.UserHistoryItem;

import java.util.List;

public interface UserHistoryService {


    List<UserHistoryItem> findHistoryByUserId(Long userId);
}
