package org.example.service;

import org.example.model.Topic;


import java.util.List;

public interface TopicService {
    Topic addTopic(String topicName);
    Topic getByTopicId(Long id);
    List<Topic> getAllTopics();
    Topic getByTopicName(String topicName);
    Topic updateTopic(Long id, String newTopicName);
    void deleteByTopicId(Long id);
}
