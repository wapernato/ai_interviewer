package org.example.service.impl;

import org.example.DAO.UserRegestration;
import org.example.DAO.impl.ImplUserRegestration;
import org.example.model.User;

public class ImplUserService {

    private final UserRegestration userRegestration;

    public ImplUserService(UserRegestration userRegestration) {
        this.userRegestration = userRegestration;
    }

    public UserRegestration getUserRegestration() { return userRegestration; }

    public void registrationName(String username){
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым");
        }
        User user = new User(username);
        userRegestration.save(user);

    }
}
