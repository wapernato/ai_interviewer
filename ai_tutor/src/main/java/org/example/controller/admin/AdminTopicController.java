package org.example.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.dto.response.TopicResponse;
import org.example.dto.topic.CreateTopicRequest;
import org.example.dto.topic.UpdateTopicRequest;
import org.example.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/topics")
public class AdminTopicController {

    private final TopicService topicService;

    public AdminTopicController(TopicService topicService){
        this.topicService = topicService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTopicId(@PathVariable @Positive(message = "ID темы должен быть положительным числом.") Long id){
        topicService.deleteByTopicId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopicById(@PathVariable @Positive(message = "ID темы должен быть положительным числом.") Long id, @Valid @RequestBody UpdateTopicRequest updateTopicRequest){
        TopicResponse topic = topicService.updateTopic(id, updateTopicRequest.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(topic);
    }



    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(@Valid @RequestBody CreateTopicRequest createTopicRequest){
        TopicResponse topic = topicService.addTopic(createTopicRequest.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(topic);
    }
}
