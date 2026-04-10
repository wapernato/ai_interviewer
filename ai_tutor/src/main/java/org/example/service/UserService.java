package org.example.service;

import org.example.model.User;

import java.util.List;

public interface UserService {
    User register(String username);
    User getById(Long id);
    List<User> getAllUsers();
    User updateUsername(Long id, String newUsername);
    void deleteUser(Long id);
}
