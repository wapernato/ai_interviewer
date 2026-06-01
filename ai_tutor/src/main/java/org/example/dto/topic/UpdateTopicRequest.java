package org.example.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateTopicRequest {

    @NotBlank(message = "Название темы не должно быть пустым.")
    @Size(min = 2, max = 100, message = "Длина названия темы должна быть от 2 до 100 символов.")
    private String name;

    public UpdateTopicRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}