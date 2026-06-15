package org.example.service.impl;


import org.example.dto.user.UserHistoryItem;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.repository.UserHistoryRepository;
import org.example.repository.UserRepository;
import org.example.service.UserHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserHistoryServiceImpl implements UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;
    private final UserRepository userRepository;

    public UserHistoryServiceImpl(UserHistoryRepository userHistoryRepository
                                 ,UserRepository userRepository
    ) {
        this.userHistoryRepository = userHistoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserHistoryItem> findHistoryByUserId(Long userId){
        if(userId == null || userId <= 0){
            throw new BadRequestException("Некорректный id.");
        }
        if(!userRepository.existsById(userId)){
           throw  new NotFoundException("Пользователя с таким id не существует.");
        }

        return userHistoryRepository.findHistoryByUserId(userId);
    }

}
