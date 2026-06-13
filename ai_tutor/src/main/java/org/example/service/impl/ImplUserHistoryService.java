package org.example.service.impl;


import org.example.dto.user.UserHistoryItem;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.repository.UserHistoryRepository;
import org.example.service.UserHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplUserHistoryService implements UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    public ImplUserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;

    }

    @Override
    public List<UserHistoryItem> findHistoryByUserId(Long userId){
        if(userId == null || userId <= 0){
            throw new BadRequestException("Некорректный id.");
        }
        userHistoryRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));


        return userHistoryRepository.findHistoryByUserId(userId);
    }

}
