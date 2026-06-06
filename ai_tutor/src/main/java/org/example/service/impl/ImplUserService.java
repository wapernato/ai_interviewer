package org.example.service.impl;

import org.example.dao.UserDAO;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplUserService implements UserService {

    private final UserDAO userDAO;

    public ImplUserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String userName){

        if(userName == null){
            throw new BadRequestException("Имя пользователя не должно быть null.");
        }

        String username = userName.trim();

        if(username.isBlank()){
            throw new BadRequestException("Имя пользователя не должно быть пустым.");
        }

        if(username.contains(" ")){
            throw new BadRequestException("Имя не должно содержать пробелы.");
        }
        if(username.length() < 2 || username.length() > 50){
            throw new BadRequestException("Длина имени должна быть от 2 до 50.");
        }

        User userFromData = userDAO.findByName(username);

        if(userFromData != null){
            throw new UserAlreadyExistsException("Пользователь с именем '" + username + "' уже существует.");
        }

        User newUser = new User(username);
        return userDAO.save(newUser);
    }

    @Override
    public User getById(Long id){
        if(id == null || id <= 0){
            throw new BadRequestException("id должен быть больше 0.");
        }
        User userFromData = userDAO.findById(id);

        if(userFromData == null){
            throw new NotFoundException("Пользователь с таким id не найден.");
        }
        return userFromData;
    }

    @Override
    public List<User> getAllUsers(){
        return userDAO.findAll();
    }

    @Override
    public User updateUsername(Long id, String newusername){

        if(id == null || id <= 0){
            throw new BadRequestException("id должен быть больше 0.");
        }

        if(newusername == null){
            throw new BadRequestException("Имя пользователя не должно быть null.");
        }

        String newUsername = newusername.trim();

        if(newUsername.isBlank()){
            throw new BadRequestException("Имя пользователя не должно быть пустым.");
        }
        if(newUsername.contains(" ")){
            throw new BadRequestException("Имя не должно содержать пробелы.");
        }
        if(newUsername.length() < 2 || newUsername.length() > 50){
            throw new BadRequestException("Длина имени должна быть от 2 до 50.");
        }

        User user = userDAO.findById(id);

        if(user == null){
            throw new NotFoundException("Такого id не существует.");
        }

        User existingUser = userDAO.findByName(newUsername);

        if(existingUser != null && !existingUser.getId().equals(id)){
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует.");
        }

        user.setUsername(newUsername);
        userDAO.update(user);
        return user;
    }

    @Override
    public void deleteById(Long id){
        if(id == null || id <= 0){
            throw new BadRequestException("Такой id некорректный.");
        }

        User user = userDAO.findById(id);

        if(user == null){
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        userDAO.deleteById(id);
    }

    @Override
    public User findByName(String userName){
        User user = userDAO.findByName(userName);

        if(userName == null){
            throw new BadRequestException("Имя пользователя не должно быть null.");
        }

        String username = userName.trim();

        if(username.isBlank()){
            throw new BadRequestException("Имя пользователя не должно быть пустым.");
        }

        if(username.contains(" ")){
            throw new BadRequestException("Имя не должно содержать пробелы.");
        }
        if(username.length() < 2 || username.length() > 50){
            throw new BadRequestException("Длина имени должна быть от 2 до 50.");
        }

        return user;
    }



}
