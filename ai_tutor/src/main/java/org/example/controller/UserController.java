package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.example.dto.user.CreateUserRequest;
import org.example.dto.user.UpdateUserRequest;
import org.example.model.User;
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
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable @Positive(message = "ID пользователя должен быть положительным числом.") Long id){
        User user = userService.getById(id);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        User user = userService.register(createUserRequest.getUsername());
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
    public ResponseEntity<User> updateUserById(@PathVariable @Positive(message = "ID пользователя должен быть положительным числом.") Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest){
        User user = userService.updateUsername(id, updateUserRequest.getNewUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @GetMapping("/search")
    public ResponseEntity<User> findByUsername(@NotBlank(message = "Имя пользователя не должно быть пустым.") @Size(min = 2, max = 50) @RequestParam String username){
        User user = userService.findByName(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
