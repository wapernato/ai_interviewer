package org.example.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.dto.response.TopicResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.exception.TopicAlreadyExistsException;
import org.example.mapper.TopicMapper;
import org.example.model.Topic;
import org.example.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {

    @Mock
    private TopicRepository topicRepository;

    private TopicServiceImpl topicService;

    private TopicMapper topicMapper;

    @BeforeEach
    void setup(){
        topicMapper = new TopicMapper();
        topicService = new TopicServiceImpl(topicRepository, topicMapper);
    }

    @Test
    void addTopic_shouldCreateTopic_whenNameIsTrimmed(){

        //arrange
        Topic savedTopic = new Topic("Java");
        savedTopic.setId(1L);

        when(topicRepository.existsByName("Java")).thenReturn(false);
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);

        //act
        TopicResponse topicResponse = topicService.addTopic(" Java ");

        //assert
        assertThat(topicResponse).isNotNull();
        assertThat(topicResponse.getName()).isEqualTo("Java");
        assertThat(topicResponse.getId()).isEqualTo(1L);
    }

    @Test
    void addTopic_shouldThrowBadRequest_whenNameIsNull(){
        assertThatThrownBy(() -> topicService.addTopic(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название новой темы не может быть пустой.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void addTopic_shouldThrowBadRequest_whenNameIsBlank(){
        assertThatThrownBy(() -> topicService.addTopic("   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название новой темы не может быть пустой.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void addTopic_shouldBadRequest_whenNameIsTooShort(){
        assertThatThrownBy(() -> topicService.addTopic("s"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void addTopic_shouldBadRequest_whenNameIsTooLong(){
        assertThatThrownBy(() -> topicService.addTopic("s".repeat(101)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void addTopic_shouldThrowTopicAlreadyExists_whenNameAlreadyExists(){

        when(topicRepository.existsByName("Java")).thenReturn(true);

        assertThatThrownBy(() -> topicService.addTopic("Java"))
                .isInstanceOf(TopicAlreadyExistsException.class)
                .hasMessage("Тема с названием 'Java' уже существует.");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void getByTopicId_shouldReturnTopic_whenIdIsNotPositive(){
        assertThatThrownBy(() -> topicService.getByTopicId(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id должен быть больше 0.");
    }

    @Test
    void getByTopicId_shouldReturnTopic_whenIdIsNull(){
        assertThatThrownBy(() -> topicService.getByTopicId(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id должен быть больше 0.");
    }

    @Test
    void getByTopicId_shouldThrowNotFound_whenTopicDoesNotExist(){
        when(topicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.getByTopicId(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема с id = 999 не найдена.");
    }

    @Test
    void allTopics_shouldReturnTopics_whenTopicsExist(){

        List<Topic> topics = List.of(
                new Topic("Java"),
                new Topic("Spring")
        );

        when(topicRepository.findAll()).thenReturn(topics);

        List<TopicResponse> result = topicService.getAllTopics();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Java");
        assertThat(result.get(1).getName()).isEqualTo("Spring");
    }

    @Test
    void allTopics_shouldReturnEmptyList_whenTopicsDoNotExist(){
        when(topicRepository.findAll()).thenReturn(List.of());

        List<TopicResponse> result = topicService.getAllTopics();

        assertThat(result);
    }

    @Test
    void getByTopicName_shouldReturnTopic_whenTopicExists(){
        Topic savedTopic = new Topic("Java");
        savedTopic.setId(1L);

        when(topicRepository.findByName("Java")).thenReturn(Optional.of(savedTopic));

        TopicResponse topicResponse = topicService.getByTopicName("Java");

        assertThat(topicResponse).isNotNull();
        assertThat(topicResponse.getName()).isEqualTo("Java");
        assertThat(topicResponse.getId()).isEqualTo(1L);
    }
}

//  getByTopicName
//
//        getByTopicName_shouldThrowBadRequest_whenNameIsNull
//        getByTopicName_shouldThrowBadRequest_whenNameIsBlank
//        getByTopicName_shouldThrowBadRequest_whenNameIsTooShort
//        getByTopicName_shouldThrowBadRequest_whenNameIsTooLong
//        getByTopicName_shouldThrowNotFound_whenTopicDoesNotExist
//  updateTopic
//        updateTopic_shouldUpdateTopic_whenDataIsValid
//        updateTopic_shouldThrowBadRequest_whenIdIsNull
//        updateTopic_shouldThrowBadRequest_whenIdIsNotPositive
//        updateTopic_shouldThrowBadRequest_whenNameIsNull
//        updateTopic_shouldThrowBadRequest_whenNameIsBlank
//        updateTopic_shouldThrowBadRequest_whenNameIsTooShort
//        updateTopic_shouldThrowBadRequest_whenNameIsTooLong
//        updateTopic_shouldThrowNotFound_whenTopicDoesNotExist
//        updateTopic_shouldThrowTopicAlreadyExists_whenNameBelongsToAnotherTopic
//   deleteByTopicId
//        deleteByTopicId_shouldDeleteTopic_whenTopicExists
//        deleteByTopicId_shouldThrowBadRequest_whenIdIsNull
//        deleteByTopicId_shouldThrowBadRequest_whenIdIsNotPositive
//        deleteByTopicId_shouldThrowNotFound_whenTopicDoesNotExist