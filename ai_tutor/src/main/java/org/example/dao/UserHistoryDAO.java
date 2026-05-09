package org.example.dao;

import org.example.dto.user.UserHistoryItem;

import java.util.List;

public interface UserHistoryDAO {

    List<UserHistoryItem> findHistoryByUserId(Long userId);
}
