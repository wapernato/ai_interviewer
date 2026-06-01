package org.example.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Size(min = 2, max = 50, message = "Длина имени пользователя должна быть от 2 до 50 символов.")
    private String username;

    public CreateUserRequest() {}

    public CreateUserRequest(String username) { this.username = username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
}
