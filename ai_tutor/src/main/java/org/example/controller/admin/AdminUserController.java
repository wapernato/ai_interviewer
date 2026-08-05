package org.example.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.dto.response.user.UserResponse;
import org.example.dto.user.UpdateUserRoleRequest;
import org.example.security.JwtService;
import org.example.service.AdminUserService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final AdminUserService adminUserService;
    private final JwtService jwtService;

    public AdminUserController(UserService userService,
                               AdminUserService adminUserService,
                               JwtService jwtService) {
        this.userService = userService;
        this.adminUserService = adminUserService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") @Positive(message = "ID пользователя должен быть положительным числом.") Long userId){
        UserResponse user = userService.getById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") @Positive(message = "ID пользователя должен быть положительным числом.") Long userId){
        userService.deleteById(userId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> findByUsername(@NotBlank(message = "Имя пользователя не должно быть пустым.") @Size(min = 2, max = 50) @RequestParam String username){
        UserResponse user = userService.findByName(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id")
            @Positive(message = "ID пользователя должен быть положительным числом.")
            Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {

        Long actorUserId = jwtService.extractUserId(jwt);
        UserResponse user = adminUserService.updateUserRole(actorUserId, userId, request.role());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
