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
public class    TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;

    public TopicServiceImpl(TopicRepository topicRepository,
                            TopicMapper topicMapper) {
        this.topicRepository = topicRepository;
        this.topicMapper = topicMapper;
    }

    @Transactional
    @Override
    public TopicResponse addTopic(String newTopicName){

        if(newTopicName == null || newTopicName.isBlank()){
            throw new BadRequestException("Название новой темы не может быть пустой.");
        }

        String topicName = newTopicName.trim();

        if(topicName.length() < 2 || topicName.length() > 100){
            throw new BadRequestException("Название новой темы должно быть от 2 до 100 символов.");
        }

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
        if(id == null || id <= 0){
            throw new BadRequestException("id должен быть больше 0.");
        }


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
        if (topicName == null) {
            throw new BadRequestException("Название темы не должно быть null.");
        }

        topicName = topicName.trim();

        if (topicName.isBlank()) {
            throw new BadRequestException("Название темы не должно быть пустым.");
        }

        if (topicName.length() < 2 || topicName.length() > 100) {
            throw new BadRequestException("Длина названия темы должна быть от 2 до 100 символов.");
        }


        String finalTopicName = topicName;
        Topic savedTopic = topicRepository.findByName(topicName)
                .orElseThrow(() -> new NotFoundException("Тема с именем - " + finalTopicName + " - не найдена."));

        return topicMapper.toResponse(savedTopic);
    }

    @Transactional
    @Override
    public TopicResponse updateTopic(Long id, String newTopicName) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id темы должен быть больше 0.");
        }

        if (newTopicName == null) {
            throw new BadRequestException("Название темы не должно быть null.");
        }

        newTopicName = newTopicName.trim();

        if (newTopicName.isBlank()) {
            throw new BadRequestException("Название темы не должно быть пустым.");
        }

        if (newTopicName.length() < 2 || newTopicName.length() > 100) {
            throw new BadRequestException("Длина названия темы должна быть от 2 до 100 символов.");
        }

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тема с id = " + id + " не найдена."));

        Optional<Topic> existingTopic = topicRepository.findByName(newTopicName);

        if (existingTopic.isPresent() && !existingTopic.get().getId().equals(id)) {
            throw new TopicAlreadyExistsException("Тема с таким названием уже существует.");
        }

        topic.setName(newTopicName);

        Topic savedTopic = topicRepository.save(topic);
        return topicMapper.toResponse(savedTopic);
    }

    @Transactional
    @Override
    public void deleteByTopicId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id темы должен быть больше 0.");
        }

        topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тема с id = " + id + " не найдена."));

        topicRepository.deleteById(id);
    }
}
