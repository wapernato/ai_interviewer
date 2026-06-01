package org.example.controller;



import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.dto.topic.CreateTopicRequest;
import org.example.dto.topic.UpdateTopicRequest;
import org.example.model.Topic;
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
    public ResponseEntity<List<Topic>> getAllTopics(){
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable @Positive(message = "ID темы всегда должен быть положительным числом.") Long id){
        Topic topic = topicService.getByTopicId(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topic);
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody CreateTopicRequest createTopicRequest){
        Topic topic = topicService.addTopic(createTopicRequest.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(topic);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTopicId(@PathVariable @Positive(message = "ID темы всегда должен быть положительным числом.") Long id){
        topicService.deleteByTopicId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopicById(@PathVariable @Positive(message = "ID темы всегда должен быть положительным числом.") Long id, @Valid @RequestBody UpdateTopicRequest updateTopicRequest){
        Topic topic = topicService.updateTopic(id, updateTopicRequest.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topic);
    }

}
