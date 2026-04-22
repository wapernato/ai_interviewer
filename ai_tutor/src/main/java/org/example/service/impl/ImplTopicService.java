package org.example.service.impl;

import org.example.DAO.TopicDAO;
import org.example.model.Topic;
import org.example.service.TopicService;

import java.util.List;

public class ImplTopicService implements TopicService {

    private final TopicDAO topicDAO;

    public ImplTopicService(TopicDAO topicRegistration) { this.topicDAO = topicRegistration; }

    public TopicDAO getUserRegistration() { return topicDAO; }

    @Override
    public Topic addTopic(String topicName){
        if(topicName == null || topicName.isBlank()){
            throw new IllegalArgumentException("Имя темы пользователя не должно быть пустым.");
        }
        if(topicName.contains(" ")){
            throw new IllegalArgumentException("Имя темы не должно содержать пробелы.");
        }
        if(topicName.length() < 2 || topicName.length() > 50){
            throw new IllegalArgumentException("Длинна темы должна быть от 2 до 50.");
        }

        Topic topic = topicDAO.findByTopicName(topicName);

        if(topic != null){
            return topic;
        }

        Topic newTopic = new Topic(topicName);
        return topicDAO.save(newTopic);

    }

    @Override
    public Topic getByTopicId(Long id) {
        if(id == null || id <= 0){
            throw new RuntimeException("id должен быть больше 0.");
        }

        Topic fromData = topicDAO.findByTopicId(id);

        if(fromData == null){
            throw new RuntimeException("Пользователь с таким id не найден.");
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
            throw new RuntimeException("Название темы не должно быть null.");
        }

        if (topicName.isBlank()) {
            throw new RuntimeException("Название темы не должно быть пустым.");
        }

        if (topicName.length() < 2 || topicName.length() > 100) {
            throw new RuntimeException("Длина названия темы должна быть от 2 до 100 символов.");
        }

        Topic topic = topicDAO.findByTopicName(topicName);

        if (topic == null) {
            throw new RuntimeException("Тема с названием '" + topicName + "' не найдена.");
        }

        return topic;
    }

    @Override
    public Topic updateTopic(Long id, String newTopicName) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Id темы должен быть больше 0.");
        }

        if (newTopicName == null) {
            throw new RuntimeException("Название темы не должно быть null.");
        }

        newTopicName = newTopicName.trim();

        if (newTopicName.isEmpty()) {
            throw new RuntimeException("Название темы не должно быть пустым.");
        }

        if (newTopicName.length() < 2 || newTopicName.length() > 100) {
            throw new RuntimeException("Длина названия темы должна быть от 2 до 100 символов.");
        }

        Topic topic = topicDAO.findByTopicId(id);
        if (topic == null) {
            throw new RuntimeException("Тема с id = " + id + " не найдена.");
        }

        Topic existingTopic = topicDAO.findByTopicName(newTopicName);
        if (existingTopic != null && !existingTopic.getId().equals(id)) {
            throw new RuntimeException("Тема с названием '" + newTopicName + "' уже существует.");
        }

        topic.setName(newTopicName);
        return topicDAO.update(topic);
    }

    @Override
    public void deleteByTopicId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Id темы должен быть больше 0.");
        }

        Topic topic = topicDAO.findByTopicId(id);
        if (topic == null) {
            throw new RuntimeException("Тема с id = " + id + " не найдена.");
        }

        topicDAO.deleteById(id);
    }
}
