package org.example.service.impl;

import org.example.dao.TopicDAO;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.exception.TopicAlreadyExistsException;
import org.example.model.Topic;
import org.example.service.TopicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplTopicService implements TopicService {

    private final TopicDAO topicDAO;

    public ImplTopicService(TopicDAO topicDAO) { this.topicDAO = topicDAO; }

    @Override
    public Topic addTopic(String topicName){

        if(topicName == null || topicName.isBlank()){
            throw new BadRequestException("Название темы не должно быть пустым.");
        }

        topicName = topicName.trim();

        if(topicName.length() < 2 || topicName.length() > 100){
            throw new BadRequestException("Длина названия темы должна быть от 2 до 100 символов.");
        }

        Topic topic = topicDAO.findByTopicName(topicName);

        if(topic != null){
            throw new TopicAlreadyExistsException("Тема с названием '" + topicName + "' уже существует.");
        }

        Topic newTopic = new Topic(topicName);
        return topicDAO.save(newTopic);

    }

    @Override
    public Topic getByTopicId(Long id) {
        if(id == null || id <= 0){
            throw new BadRequestException("id должен быть больше 0.");
        }

        Topic fromData = topicDAO.findByTopicId(id);

        if(fromData == null){
            throw new NotFoundException("Тема с id = " + id + " не найдена.");
        }

        return fromData;
    }

    @Override
    public List<Topic> getAllTopics(){
        return topicDAO.findAll();
    }

    @Override
    public Topic getByTopicName(String topicName){
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

        Topic topic = topicDAO.findByTopicName(topicName);

        if (topic == null) {
            throw new NotFoundException("Тема с названием '" + topicName + "' не найдена.");
        }

        return topic;
    }

    @Override
    public Topic updateTopic(Long id, String newTopicName) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id темы должен быть больше 0.");
        }

        if (newTopicName == null) {
            throw new BadRequestException("Название темы не должно быть null.");
        }

        newTopicName = newTopicName.trim();

        if (newTopicName.isEmpty()) {
            throw new BadRequestException("Название темы не должно быть пустым.");
        }

        if (newTopicName.length() < 2 || newTopicName.length() > 100) {
            throw new BadRequestException("Длина названия темы должна быть от 2 до 100 символов.");
        }

        Topic topic = topicDAO.findByTopicId(id);
        if (topic == null) {
            throw new NotFoundException("Тема с id = " + id + " не найдена.");
        }

        Topic existingTopic = topicDAO.findByTopicName(newTopicName);
        if (existingTopic != null && !existingTopic.getId().equals(id)) {
            throw new TopicAlreadyExistsException("Тема с названием '" + newTopicName + "' уже существует.");
        }

        topic.setName(newTopicName);
        return topicDAO.update(topic);
    }

    @Override
    public void deleteByTopicId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id темы должен быть больше 0.");
        }

        Topic topic = topicDAO.findByTopicId(id);
        if (topic == null) {
            throw new NotFoundException("Тема с id = " + id + " не найдена.");
        }

        topicDAO.deleteById(id);
    }
}
