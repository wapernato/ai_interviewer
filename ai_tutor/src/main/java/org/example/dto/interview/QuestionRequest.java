package org.example.dto.interview;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuestionRequest {
    @NotBlank(message = "Тема не должна быть пустой.")
    @Size(min = 2, max = 200, message = "Тема должна содержать от 2 до 200 символов.")
    private String topic;

    public QuestionRequest() {}

    public QuestionRequest(String topic){
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
