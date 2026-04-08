package org.example.DAO;

import org.example.model.User;

public interface UserRegestration {
    User save(User user);
    User findByUserName(String username);
}
