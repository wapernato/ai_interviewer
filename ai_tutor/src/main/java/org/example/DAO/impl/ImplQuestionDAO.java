package org.example.DAO.impl;

import org.example.DAO.QuestionDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImplQuestionDAO implements QuestionDAO {

    private final DBConnection dbConnection;

    public ImplQuestionDAO(DBConnection dbConnection) { this.dbConnection = dbConnection; }

    @Override
    public Question save(Question question) {
        String sql = """
            INSERT INTO questions(user_id, topic_id, text_question, source, language)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (
                Connection connection = dbConnection.getConnectionDB();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setLong(1, question.getUserId());
            preparedStatement.setLong(2, question.getTopicId());
            preparedStatement.setString(3, question.getTextQuestion());

            if (question.getSource() == null || question.getSource().isBlank()) {
                preparedStatement.setString(4, "manual");
            } else {
                preparedStatement.setString(4, question.getSource());
            }

            if (question.getLanguage() == null || question.getLanguage().isBlank()) {
                preparedStatement.setString(5, "ru");
            } else {
                preparedStatement.setString(5, question.getLanguage());
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong("id");
                    question.setId(generatedId);
                }
            }

            return question;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении вопроса", e);
        }
    }
}
