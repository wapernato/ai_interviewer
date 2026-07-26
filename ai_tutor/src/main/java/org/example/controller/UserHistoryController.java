package org.example.controller;

import org.example.dto.user.UserHistoryItem;
import org.example.security.JwtService;
import org.example.service.UserHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class UserHistoryController {

    private final UserHistoryService userHistoryService;
    private final JwtService jwtService;

    public UserHistoryController(UserHistoryService userHistoryService,
                                 JwtService jwtService){
        this.userHistoryService = userHistoryService;
        this.jwtService = jwtService;
    }
    @GetMapping("/me/interview-history")
    public ResponseEntity<List<UserHistoryItem>> userHistory(@AuthenticationPrincipal Jwt jwt){
        Long currentId = jwtService.extractUserId(jwt);
        List<UserHistoryItem> userHistoryItems = userHistoryService.findHistoryByUserId(currentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userHistoryItems);
    }
}
