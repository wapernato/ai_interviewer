package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.example.dto.response.UserResponse;
import org.example.dto.user.CreateUserRequest;
import org.example.dto.user.UpdateUserRequest;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
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
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Positive(message = "ID пользователя должен быть положительным числом.") Long id){
        UserResponse user = userService.getById(id);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        UserResponse user = userService.register(createUserRequest.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable @Positive(message = "ID пользователя должен быть положительным числом.") Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable @Positive(message = "ID пользователя должен быть положительным числом.") Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse user = userService.updateUsername(id, updateUserRequest.getNewUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> findByUsername(@NotBlank(message = "Имя пользователя не должно быть пустым.") @Size(min = 2, max = 50) @RequestParam String username){
        UserResponse user = userService.findByName(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
