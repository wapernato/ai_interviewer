package org.example.controller;



import jakarta.validation.constraints.*;
import org.example.dto.response.TopicResponse;
import org.example.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<TopicResponse>> getAllTopics(){
        List<TopicResponse> topics = topicService.getAllTopics();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable @Positive(message = "ID темы должен быть положительным числом.") Long id){
        TopicResponse topic = topicService.getByTopicId(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topic);
    }

    @GetMapping("/search")
    public ResponseEntity<TopicResponse> findByName(@NotBlank(message = "Имя не должно быть пустое.") @Size(min = 2, max = 50) @RequestParam String topicName){
        TopicResponse topic = topicService.getByTopicName(topicName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topic);
    }


}
