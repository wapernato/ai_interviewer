package org.example.service;

import org.example.dto.response.TopicResponse;


import java.util.List;

public interface TopicService {
    TopicResponse addTopic(String topicName);
    TopicResponse getByTopicId(Long id);
    List<TopicResponse> getAllTopics();
    TopicResponse getByTopicName(String topicName);
    TopicResponse updateTopic(Long id, String newTopicName);
    void deleteByTopicId(Long id);
}
