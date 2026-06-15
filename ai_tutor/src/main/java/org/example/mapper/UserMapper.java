package org.example.mapper;

import org.example.dto.response.UserResponse;
import org.example.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if(user == null){
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());

        return userResponse;
    }

    public List<UserResponse> toResponseList(List<User> users) {
        if(users == null){
            return List.of();
        }

        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}
