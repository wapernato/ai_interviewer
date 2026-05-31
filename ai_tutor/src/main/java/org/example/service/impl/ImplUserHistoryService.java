package org.example.service.impl;

import org.example.dao.UserDAO;
import org.example.dao.UserHistoryDAO;
import org.example.dto.user.UserHistoryItem;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.service.UserHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplUserHistoryService implements UserHistoryService {

    private final UserHistoryDAO userHistoryDAO;
    private final UserDAO userDAO;

    public ImplUserHistoryService(UserHistoryDAO userHistoryDAO, UserDAO userDAO) {
        this.userHistoryDAO = userHistoryDAO;
        this.userDAO = userDAO;
    }

    @Override
    public List<UserHistoryItem> findHistoryByUserId(Long userId){
        if(userId == null || userId <= 0){
            throw new BadRequestException("Некорректный id.");
        }
        if(userDAO.findById(userId) == null){
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        return userHistoryDAO.findHistoryByUserId(userId);
    }

}
