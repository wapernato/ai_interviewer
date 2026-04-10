package org.example.DAO;

import org.example.model.User;

import java.util.List;

public interface UserDAO {
    User save(User user);
    User findByName(String username);
    User findById(Long id);
    List<User> findAll();
    User update(User user);
    void deleteById(Long id);
}
