package org.example.controller.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.example.dto.response.UserResponse;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
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
}
