package org.example.controller;

import org.example.dto.user.UserHistoryItem;
import org.example.model.User;
import org.example.service.UserHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserHistoryController {

    private final UserHistoryService userHistoryService;

    public UserHistoryController(UserHistoryService userHistoryService){
        this.userHistoryService = userHistoryService;
    }
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<UserHistoryItem>> userHistory(@PathVariable  Long userId){
        List<UserHistoryItem> userHistoryItems = userHistoryService.findHistoryByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userHistoryItems);
    }
}
