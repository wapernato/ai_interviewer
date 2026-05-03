package org.example.dao.impl;

import org.example.DBConnection.DBConnection;
import org.example.dao.UserHistoryDAO;
import org.example.dto.UserHistoryItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImplUserHistoryDAO implements UserHistoryDAO {

    private final DBConnection dbConnection;

    public ImplUserHistoryDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<UserHistoryItem>  findHistoryByUserId(Long userId){
        String sql = """
                SELECT  que.id AS question_id,
                        us.username AS username,
                        top.name AS topic_name,
                        que.text_question AS text_question,
                        ans.answer_text AS answer_text,
                        ans.model_name AS model_name
                FROM questions que
                JOIN users us ON us.id = que.user_id
                LEFT JOIN topics top ON que.topic_id = top.id
                LEFT JOIN answers ans ON que.id = ans.question_id
                WHERE que.user_id = ?
                ORDER BY que.id ASC
               """;
        List<UserHistoryItem> historyItems = new ArrayList<>();
        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ){
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    UserHistoryItem userHistoryItem = new UserHistoryItem();

                    userHistoryItem.setQuestionId(resultSet.getLong("question_id"));
                    userHistoryItem.setUsername(resultSet.getString("username"));
                    userHistoryItem.setTopicName(resultSet.getString("topic_name"));
                    userHistoryItem.setTextQuestion(resultSet.getString("text_question"));
                    userHistoryItem.setAnswerText(resultSet.getString("answer_text"));
                    userHistoryItem.setModelName(resultSet.getString("model_name"));

                    historyItems.add(userHistoryItem);
                }
                return historyItems;
            }
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске истории по Id пользователя.");
        }
    }
}
