package org.example.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class PasswordStrengthRequest {
    @NotBlank(message = "Пароль не должен быть пустым.")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
