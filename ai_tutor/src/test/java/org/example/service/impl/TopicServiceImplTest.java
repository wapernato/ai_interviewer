package org.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.dto.response.TopicResponse;
import org.example.mapper.TopicMapper;
import org.example.model.Topic;
import org.example.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    private TopicServiceImpl topicService;

    private TopicMapper topicMapper;

    @Test
    void addTopic_shouldCreateTopic_whenNameIsTrimmed(){

        //arrange
        Topic savedTopic = new Topic("Java");
        savedTopic.setId(1L);

        when(topicRepository.existsByName("Java")).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        topicMapper = new TopicMapper();
        topicService = new TopicServiceImpl(topicRepository, topicMapper);

        //act
        TopicResponse topicResponse = topicService.addTopic(" Java ");

        //assert
        assertThat(topicResponse).isNotNull();
        assertThat(topicResponse.getName()).isEqualTo("Java");
        assertThat(topicResponse.getId()).isEqualTo(1L);


    }
}
