package org.example.service;

import org.example.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse register(String username);
    UserResponse getById(Long id);
    UserResponse findByName(String username);
    List<UserResponse> getAllUsers();
    UserResponse updateUsername(Long id, String newUsername);
    void deleteById(Long id);

}
