package org.example.controller;



import org.example.dto.CreateTopicRequest;
import org.example.model.Topic;
import org.example.service.TopicService;
import org.springframework.web.bind.annotation.*;

import java.net.CacheRequest;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public List<Topic> getAllTopic(){
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

}
