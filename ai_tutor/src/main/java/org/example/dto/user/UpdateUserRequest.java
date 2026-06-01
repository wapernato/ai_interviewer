package org.example.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    @Size(min = 2, max = 50, message = "Длина имени пользователя должна быть от 2 до 50 символов.")
    private String username;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String newUsername){ this.username = newUsername; }

    public void setNewUsername(String newUsername) {
        this.username = newUsername;
    }

    public String getNewUsername() {
        return username;
    }
}
