package org.example.controller;

import org.example.dto.user.CreateUserRequest;
import org.example.dto.user.UpdateUserRequest;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getById(id);
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest createUserRequest){
        return userService.register(createUserRequest.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public User updateUserById(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest){
        return userService.updateUsername(id, updateUserRequest.getNewUsername());
    }

}
