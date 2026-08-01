package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.response.UserResponse;
import org.example.dto.user.UpdateUserRequest;
import org.example.dto.user.UserStatisticsResponse;
import org.example.security.JwtService;
import org.example.service.UserService;
import org.example.service.UserStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserStatisticsService userStatisticsService;

    public UserController(UserService userService,
                          JwtService jwtService,
                          UserStatisticsService userStatisticsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userStatisticsService = userStatisticsService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal Jwt jwt){
        Long currentId = jwtService.extractUserId(jwt);
        UserResponse user = userService.getById(currentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpdateUserRequest updateUserRequest){
        Long currentId = jwtService.extractUserId(jwt);
        UserResponse user = userService.updateUsername(currentId, updateUserRequest.getNewUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal Jwt jwt){
        Long currentId = jwtService.extractUserId(jwt);
        userService.deleteById(currentId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticsResponse> getMyStatistics(@AuthenticationPrincipal Jwt jwt){
        Long currentId = jwtService.extractUserId(jwt);
        UserStatisticsResponse response = userStatisticsService.getUserStatistics(currentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }


}
