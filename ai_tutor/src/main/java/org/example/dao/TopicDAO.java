package org.example.dao;

import org.example.model.Topic;

import java.util.List;

public interface TopicDAO {
    Topic save(Topic topic);
    Topic findByTopicName(String topicName);
    Topic findByTopicId(Long id);
    List<Topic> findAll();
    Topic update(Topic topic);
    void deleteById(Long id);

}
