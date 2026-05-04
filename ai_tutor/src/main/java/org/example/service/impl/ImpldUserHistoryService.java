package org.example.service.impl;

import org.example.dao.UserDAO;
import org.example.dao.UserHistoryDAO;
import org.example.dto.UserHistoryItem;
import org.example.service.UserHistoryService;

import java.util.ArrayList;
import java.util.List;

public class ImpldUserHistoryService implements UserHistoryService {

    private final UserHistoryDAO userHistoryDAO;
    private final UserDAO userDAO;

    public ImpldUserHistoryService(UserHistoryDAO userHistoryDAO, UserDAO userDAO) {
        this.userHistoryDAO = userHistoryDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<UserHistoryItem> findHistoryByUserId(Long userId){
        if(userId == null || userId <= 0){
            throw new RuntimeException("Некорректный id.");
        }
        if(userDAO.findById(userId) == null){
            throw new RuntimeException("Пользователя с таким id не существует.");
        }
        return userHistoryDAO.findHistoryByUserId(userId);
    }

}
