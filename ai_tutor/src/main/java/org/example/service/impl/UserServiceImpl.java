package org.example.service.impl;

import org.example.dto.response.UserResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserResponse register(String userName){

        if(userName == null || userName.isBlank()){
            throw new BadRequestException("Имя пользователя не должно быть пустым.");
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

        Optional<User> userFromData = userRepository.findByUsername(username);

        if(userFromData.isPresent()){
            throw new UserAlreadyExistsException("Пользователь с именем '" + username + "' уже существует.");
        }

        User newUser = new User(username);
        User savedUser = userRepository.save(newUser);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getById(Long id){
        if(id == null || id <= 0){
            throw new BadRequestException("id должен быть больше 0.");
        }
        User savedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> getAllUsers(){
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Transactional
    @Override
    public UserResponse updateUsername(Long id, String newusername){

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

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));


        Optional<User> existingUser = userRepository.findByUsername(newUsername);

        if(existingUser.isPresent() && !existingUser.get().getId().equals(id)){
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует.");
        }

        user.setUsername(newUsername);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Override
    public void deleteById(Long id){
        if(id == null || id <= 0){
            throw new BadRequestException("Такой id некорректный.");
        }

        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));


        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse findByName(String userName){

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

        User savedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь с именем (" + username + ") не найден."));

        return userMapper.toResponse(savedUser);
    }
}
