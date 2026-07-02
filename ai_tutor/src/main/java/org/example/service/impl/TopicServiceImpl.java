package org.example.service.impl;

import org.example.dto.response.TopicResponse;
import org.example.mapper.TopicMapper;
import org.springframework.transaction.annotation.Transactional;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.exception.TopicAlreadyExistsException;
import org.example.model.Topic;
import org.example.repository.TopicRepository;
import org.example.service.TopicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    public TopicServiceImpl(TopicRepository topicRepository,
                            TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id темы должен быть больше 0.");
        }
    }

    private String normalizeAndValidateTopicName(String name) {
        if (name == null) {
            throw new BadRequestException("Название темы не должно быть null.");
        }

        String topicName = name.trim();

        if (topicName.isBlank()) {
            throw new BadRequestException("Название темы не должно быть пустым.");
        }

        if(topicName.length() < 2 || topicName.length() > 100){
            throw new BadRequestException("Длина названия новой темы должно быть от 2 до 100 символов.");
        }

        return topicName;
    }


    @Transactional
    @Override
    public TopicResponse addTopic(String newTopicName){
        String topicName = normalizeAndValidateTopicName(newTopicName);

        if (topicRepository.existsByName(topicName)) {
            throw new TopicAlreadyExistsException("Тема с названием '" + topicName + "' уже существует.");
        }

        Topic newTopic = new Topic(topicName);
        Topic savedTopic = topicRepository.save(newTopic);
        return topicMapper.toResponse(savedTopic);
    }

    @Transactional(readOnly = true)
    @Override
    public TopicResponse getByTopicId(Long id) {
        validateId(id);

        Topic savedTopic = topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тема с id = " + id + " не найдена."));

        return topicMapper.toResponse(savedTopic);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TopicResponse> getAllTopics(){
        return topicMapper.toResponseList(topicRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public TopicResponse getByTopicName(String topicName){

        String finalTopicName = normalizeAndValidateTopicName(topicName);
        Topic savedTopic = topicRepository.findByName(finalTopicName)
                .orElseThrow(() -> new NotFoundException("Тема с именем - " + finalTopicName + " - не найдена."));

        return topicMapper.toResponse(savedTopic);
    }

    @Transactional
    @Override
    public TopicResponse updateTopic(Long id, String newTopicName) {
        validateId(id);
        String topicName = normalizeAndValidateTopicName(newTopicName);

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тема с id = " + id + " не найдена."));

        Optional<Topic> existingTopic = topicRepository.findByName(topicName);

        if (existingTopic.isPresent() && !existingTopic.get().getId().equals(id)) {
            throw new TopicAlreadyExistsException("Тема с таким названием уже существует.");
        }

        topic.setName(topicName);

        Topic savedTopic = topicRepository.save(topic);
        return topicMapper.toResponse(savedTopic);
    }

    @Transactional
    @Override
    public void deleteByTopicId(Long id) {
        validateId(id);
        topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тема с id = " + id + " не найдена."));

        topicRepository.deleteById(id);
    }
}
