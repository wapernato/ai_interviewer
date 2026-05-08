package org.example.controller;



import org.example.dto.CreateTopicRequest;
import org.example.dto.UpdateTopicRequest;
import org.example.model.Topic;
import org.example.service.TopicService;
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
    public List<Topic> getAllTopics(){
        return topicService.getAllTopics();
    }

    @GetMapping("/{id}")
    public Topic getTopicById(@PathVariable Long id){
        return topicService.getByTopicId(id);
    }

    @PostMapping
    public Topic createTopic(@RequestBody CreateTopicRequest createTopicRequest){
        return topicService.addTopic(createTopicRequest.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByTopicId(@PathVariable Long id){
        topicService.deleteByTopicId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public Topic updateTopicById(@PathVariable Long id, @RequestBody UpdateTopicRequest updateTopicRequest){
        return topicService.updateTopic(id, updateTopicRequest.getName());
    }

}
