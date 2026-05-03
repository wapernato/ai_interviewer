package org.example.dao.impl;

import org.example.dao.TopicDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.Topic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImplTopicDAO implements TopicDAO {

    private final DBConnection dbConnection;

    public ImplTopicDAO(DBConnection dbConnection) { this.dbConnection = dbConnection; }

    @Override
    public Topic save(Topic topic){
        String sql = "insert into topics(name) values (?) returning id";
        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){

            preparedStatement.setString(1, topic.getName());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    Long generatedId = resultSet.getLong("id");
                    topic.setId(generatedId);
                }
            }
            return topic;
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при сохранении темы пользователя.", e);
        }
    }

    @Override
    public Topic findByTopicId(Long id) {
        String sql = "select id, name from topics where id = ?";
        try(Connection connection = dbConnection.getConnectionDB();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){

            preparedStatement.setLong(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    Topic newTopic = new Topic();
                    newTopic.setId(resultSet.getLong("id"));
                    newTopic.setName(resultSet.getString("name"));
                    return newTopic;
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске темы по id.", e);
        }
        return null;
    }

    @Override
    public Topic findByTopicName(String topicName){
        String sql = "select id, name from topics where name = ?";
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, topicName);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    Topic newTopic = new Topic();
                    newTopic.setId(resultSet.getLong("id"));
                    newTopic.setName(resultSet.getString("name"));
                    return newTopic;
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске темы по имени", e);
        }
        return null;
    }

    @Override
    public List<Topic> findAll() {
        String sql = "select id, name from topics";
        List<Topic> listOfTopics = new ArrayList<>();
        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Topic newTopic = new Topic();
                    newTopic.setId(resultSet.getLong("id"));
                    newTopic.setName(resultSet.getString("name"));
                    listOfTopics.add(newTopic);
                    }
                }
            }
        catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске всех тем.", e);
        }
        return listOfTopics;
    }

    @Override
    public Topic update(Topic topic){
        String sql = "update topics set name = ? where id = ?";
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, topic.getName());
            preparedStatement.setLong(2, topic.getId());

            int rowsUpdate = preparedStatement.executeUpdate();

            if(rowsUpdate == 0){
                throw new RuntimeException("Пользователь с id = " + topic.getId() + " не найден");
            }

            return topic;
        }
        catch (SQLException e){
            throw new RuntimeException("Произошла ошибка при обновлении темы.", e);
        }
    }

    @Override
    public void deleteById(Long id){
        String sql = "delete from topics where id = ?";

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setLong(1, id);
            int rowsDelete = preparedStatement.executeUpdate();

            if(rowsDelete == 0){
                throw new RuntimeException("Тема с таким id = " + id + " не найден");
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при удалении темы по id." ,e);
        }

    }
}





















