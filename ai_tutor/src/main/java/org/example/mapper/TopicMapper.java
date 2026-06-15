package org.example.mapper;

import org.example.dto.response.TopicResponse;
import org.example.model.Topic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopicMapper {

    public TopicResponse toResponse(Topic topic) {
        if(topic == null){
            return null;
        }

        TopicResponse topicResponse = new TopicResponse();

        topicResponse.setId(topic.getId());
        topicResponse.setName(topic.getName());

        return topicResponse;
    }

    public List<TopicResponse> toResponseList(List<Topic> topics) {
        if(topics == null){
            return List.of();
        }

        return topics.stream()
                .map(this::toResponse)
                .toList();
    }
}
