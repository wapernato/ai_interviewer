package org.example.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    @Size(min = 2, max = 50, message = "Имя пользователя должно быть от 2 до 50 символов.")
    private String username;

    @NotBlank(message = "Email не должен быть пустым.")
    @Email(message = "Email должен быть корректным.")
    @Size(max = 100, message = "Email должен быть не длиннее 100 символов.")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым.")
    @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
