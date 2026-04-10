package org.example.service.impl;

import org.example.DAO.UserDAO;
import org.example.model.User;
import org.example.service.UserService;

public class ImplUserService implements UserService {

    private final UserDAO userRegistration;

    public ImplUserService(UserDAO userRegestration) {
        this.userRegistration = userRegestration;
    }

    public UserDAO getUserRegistration() { return userRegistration; }

    public User register(String username){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым.");
        }
        if(username.contains(" ")){
            throw new IllegalArgumentException("Имя не должно содержать пробелы.");
        }
        if(username.length() < 2 || username.length() > 50){
            throw new IllegalArgumentException("Длинна имени должна быть от 2 до 50.");
        }

        User userFromData = userRegistration.findByName(username);

        if(userFromData != null){
            return userFromData;
        }

        User newUser = new User(username);
        return userRegistration.save(newUser);
    }


}
