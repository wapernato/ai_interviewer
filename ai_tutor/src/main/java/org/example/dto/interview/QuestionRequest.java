package org.example.dto.interview;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class QuestionRequest {
    @NotBlank(message = "Тема не должна быть пустой.")
    @Size(min = 2, max = 200, message = "Тема должна содержать от 2 до 200 символов.")
    private String topic;
    @NotNull(message = "AI-профиль должен быть выбран.")
    @Positive(message = "ID AI-профиля должен быть положительным.")
    private Long aiProfileId;

    public QuestionRequest() {}

    public QuestionRequest(String topic){
        this.topic = topic;
    }

    public Long getAiProfileId(){
        return aiProfileId;
    }

    public void setAiProfileId(Long aiProfileId){
        this.aiProfileId = aiProfileId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
