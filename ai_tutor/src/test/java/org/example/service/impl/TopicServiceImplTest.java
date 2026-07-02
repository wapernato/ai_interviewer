package org.example.service.impl;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
        verify(topicRepository).save(argThat(topic -> "Java".equals(topic.getName())));
    }

    @Test
    void addTopic_shouldThrowBadRequest_whenNameIsNull(){
        assertThatThrownBy(() -> topicService.addTopic(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не должно быть null.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void addTopic_shouldThrowBadRequest_whenNameIsBlank(){
        assertThatThrownBy(() -> topicService.addTopic("   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не должно быть пустым.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void addTopic_shouldBadRequest_whenNameIsTooShort(){
        assertThatThrownBy(() -> topicService.addTopic("s"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина названия новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void addTopic_shouldBadRequest_whenNameIsTooLong(){
        assertThatThrownBy(() -> topicService.addTopic("s".repeat(101)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина названия новой темы должно быть от 2 до 100 символов.");
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
    void getByTopicId_shouldReturnTopic_whenTopicExists(){
        Topic savedTopic = new Topic("Java");
        savedTopic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(savedTopic));

        TopicResponse topicResponse = topicService.getByTopicId(1L);

        assertThat(topicResponse).isNotNull();
        assertThat(topicResponse.getId()).isEqualTo(1L);
        assertThat(topicResponse.getName()).isEqualTo("Java");
    }

    @Test
    void getByTopicId_shouldThrowBadRequest_whenIdIsNotPositive(){
        assertThatThrownBy(() -> topicService.getByTopicId(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id темы должен быть больше 0.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void getByTopicId_shouldThrowBadRequest_whenIdIsNull(){
        assertThatThrownBy(() -> topicService.getByTopicId(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id темы должен быть больше 0.");

        verifyNoInteractions(topicRepository);
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

        assertThat(result).isEmpty();
    }

    @Test
    void getByTopicName_shouldReturnTopic_whenTopicExists(){
        Topic savedTopic = new Topic("Java");
        savedTopic.setId(1L);

        when(topicRepository.findByName("Java")).thenReturn(Optional.of(savedTopic));

        TopicResponse topicResponse = topicService.getByTopicName(" Java ");

        assertThat(topicResponse).isNotNull();
        assertThat(topicResponse.getName()).isEqualTo("Java");
        assertThat(topicResponse.getId()).isEqualTo(1L);
    }

    @Test
    void getByTopicName_shouldThrowBadRequest_whenNameIsNull(){
        assertThatThrownBy(() -> topicService.getByTopicName(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не должно быть null.");
    }

    @Test
    void getByTopicName_shouldThrowBadRequest_whenNameIsBlank(){
        assertThatThrownBy(() -> topicService.getByTopicName(""))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не должно быть пустым.");
    }

    @Test
    void getByTopicName_shouldThrowBadRequest_whenNameIsTooShort(){
        assertThatThrownBy(() -> topicService.getByTopicName("a"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина названия новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void getByTopicName_shouldThrowBadRequest_whenNameIsTooLong(){
        assertThatThrownBy(() -> topicService.getByTopicName("a".repeat(101)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина названия новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void getByTopicName_shouldThrowNotFound_whenTopicDoesNotExist(){
        Optional<Topic> optionalTopic = Optional.empty();
        when(topicRepository.findByName("Java")).thenReturn(optionalTopic);

        assertThatThrownBy(() -> topicService.getByTopicName("Java"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема с именем - Java - не найдена.");
    }

    @Test
    void updateTopic_shouldUpdateTopic_whenDataIsValid(){
        Topic topicToUpdate = new Topic("Spring");
        topicToUpdate.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topicToUpdate));
        when(topicRepository.findByName("Java")).thenReturn(Optional.empty());
        when(topicRepository.save(topicToUpdate)).thenReturn(topicToUpdate);

        TopicResponse topicResponse = topicService.updateTopic(1L, " Java ");

        assertThat(topicResponse).isNotNull();
        assertThat(topicResponse.getId()).isEqualTo(1L);
        assertThat(topicResponse.getName()).isEqualTo("Java");
        verify(topicRepository).save(topicToUpdate);
    }

    @Test
    void updateTopic_shouldUpdateTopic_whenNameBelongsToSameTopic(){
        Topic topicToUpdate = new Topic("Java");
        topicToUpdate.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topicToUpdate));
        when(topicRepository.findByName("Java")).thenReturn(Optional.of(topicToUpdate));
        when(topicRepository.save(topicToUpdate)).thenReturn(topicToUpdate);

        TopicResponse topicResponse = topicService.updateTopic(1L, "Java");

        assertThat(topicResponse.getId()).isEqualTo(1L);
        assertThat(topicResponse.getName()).isEqualTo("Java");
        verify(topicRepository).save(topicToUpdate);
    }

    @Test
    void updateTopic_shouldThrowBadRequest_whenIdIsNull(){
        assertThatThrownBy(() -> topicService.updateTopic(null, "Java"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id темы должен быть больше 0.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void updateTopic_shouldThrowBadRequest_whenIdIsNotPositive(){
        assertThatThrownBy(() -> topicService.updateTopic(-1L, "Java"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id темы должен быть больше 0.");
    }

    @Test
    void updateTopic_shouldThrowBadRequest_whenNameIsBlank(){
        assertThatThrownBy(() -> topicService.updateTopic(1L, ""))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не должно быть пустым.");
    }

    @Test
    void updateTopic_shouldThrowBadRequest_whenNameIsNull(){
        assertThatThrownBy(() -> topicService.updateTopic(1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название темы не должно быть null.");
    }

    @Test
    void updateTopic_shouldThrowBadRequest_whenNameIsTooShort(){
        assertThatThrownBy(() -> topicService.updateTopic(1L, "a"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина названия новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void updateTopic_shouldThrowBadRequest_whenNameIsTooLong(){
        assertThatThrownBy(() -> topicService.updateTopic(1L, "a".repeat(101)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина названия новой темы должно быть от 2 до 100 символов.");
    }

    @Test
    void updateTopic_shouldThrowNotFound_whenTopicDoesNotExist(){
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.updateTopic(1L, "Java"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема с id = 1 не найдена.");

        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void updateTopic_shouldThrowTopicAlreadyExists_whenNameBelongsToAnotherTopic() {
        // arrange
        Topic updatedTopic = new Topic("Spring");
        updatedTopic.setId(1L);

        Topic existingTopic = new Topic("Java");
        existingTopic.setId(2L);

        when(topicRepository.findById(1L))
                .thenReturn(Optional.of(updatedTopic));

        when(topicRepository.findByName("Java"))
                .thenReturn(Optional.of(existingTopic));

        // act + assert
        assertThatThrownBy(() -> topicService.updateTopic(1L, "Java"))
                .isInstanceOf(TopicAlreadyExistsException.class)
                .hasMessage("Тема с таким названием уже существует.");

        verify(topicRepository, never()).save(any(Topic.class));
        assertThat(updatedTopic.getName()).isEqualTo("Spring");
    }

    @Test
    void deleteByTopicId_shouldDeleteTopic_whenTopicExists(){

        Topic savedTopic = new Topic("Java");
        savedTopic.setId(1L);

        when(topicRepository.findById(1L)).thenReturn(Optional.of(savedTopic));

        topicService.deleteByTopicId(1L);

        verify(topicRepository).deleteById(1L);
    }

    @Test
    void deleteByTopicId_shouldThrowBadRequest_whenIdIsNull(){
        assertThatThrownBy(() -> topicService.deleteByTopicId(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id темы должен быть больше 0.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void deleteByTopicId_shouldThrowBadRequest_whenIdIsNotPositive(){
        assertThatThrownBy(() -> topicService.deleteByTopicId(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id темы должен быть больше 0.");

        verifyNoInteractions(topicRepository);
    }

    @Test
    void deleteByTopicId_shouldThrowNotFound_whenTopicDoesNotExist(){

        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicService.deleteByTopicId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема с id = 1 не найдена.");

        verify(topicRepository, never()).deleteById(anyLong());
    }
}
