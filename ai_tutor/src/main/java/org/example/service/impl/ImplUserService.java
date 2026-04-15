package org.example.service.impl;

import org.example.DAO.UserDAO;
import org.example.model.User;
import org.example.service.UserService;

import java.util.List;

public class ImplUserService implements UserService {

    private final UserDAO userDAO;

    public ImplUserService(UserDAO userRegestration) {
        this.userDAO = userRegestration;
    }

    public UserDAO getUserRegistration() { return userDAO; }

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

        User userFromData = userDAO.findByName(username);

        if(userFromData != null){
            return userFromData;
        }

        User newUser = new User(username);
        return userDAO.save(newUser);
    }

    @Override
    public User getById(Long id){
        if(id == null || id <= 0){
            throw new RuntimeException("id должен быть больше 0.");
        }
        User userFromData = userDAO.findById(id);

        if(userFromData == null){
            throw new RuntimeException("Пользователь с таким id не найден.");
        }
        return userFromData;
    }

    @Override
    public List<User> getAllUsers(){
        return userDAO.findAll();
    }

    @Override
    public User updateUsername(Long id, String newUsername){

        if(id == null || id <= 0){
            throw new RuntimeException("id должен быть больше 0.");
        }
        if(newUsername == null || newUsername.isBlank()){
            throw new RuntimeException("Имя пользователя не должно быть пустым.");
        }
        if(newUsername.contains(" ")){
            throw new RuntimeException("Имя не должно содержать пробелы.");
        }
        if(newUsername.length() < 2 || newUsername.length() > 50){
            throw new RuntimeException("Длинна имени должна быть от 2 до 50.");
        }

        User user = userDAO.findById(id);

        if(user == null){
            throw new RuntimeException("Такого id не существует.");
        }

        User existingUser = userDAO.findByName(newUsername);

        if(existingUser != null && !existingUser.getId().equals(id)){
            throw new RuntimeException("Пользователь с таким именем уже существует.");
        }

        user.setUsername(newUsername);
        userDAO.update(user);
        return user;
    }

    @Override
    public void deleteById(Long id){
        if(id == null || id <= 0){
            throw new RuntimeException("Такой id некорректный.");
        }

        User user = userDAO.findById(id);

        if(user == null){
            throw new RuntimeException("Пользователь с таким id не найден");
        }

        userDAO.deleteById(id);
    }




}
