package org.example.dao.impl;

import org.example.dao.QuestionDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ImplQuestionDAO implements QuestionDAO {

    private final DBConnection dbConnection;

    public ImplQuestionDAO(DBConnection dbConnection) { this.dbConnection = dbConnection; }

    private Question mapResultSetToQuestion(ResultSet resultSet) throws SQLException{
        Question question = new Question();

        question.setId(resultSet.getLong("id"));
        question.setUserId(resultSet.getLong("user_id"));
        question.setTopicId(resultSet.getObject("topic_id", Long.class));
        question.setTextQuestion(resultSet.getString("text_question"));
        question.setSource(resultSet.getString("source"));
        question.setLanguage(resultSet.getString("language"));

        return question;
    }

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
            if (question.getTopicId() == null) {
                preparedStatement.setNull(2, java.sql.Types.BIGINT);
            } else {
                preparedStatement.setLong(2, question.getTopicId());
            }
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
                    return question;
                }
            }

            throw new RuntimeException("Вопрос не был сохранён.");

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении вопроса.", e);
        }
    }

    @Override
    public Question findById(Long id) {
        String sql = """
                SELECT id, user_id, topic_id, text_question, source, language
                FROM questions
                WHERE id = ?
                """;


        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    return mapResultSetToQuestion(resultSet);
                }
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске вопроса по id.", e);
        }
        return null;
    }

    @Override
    public List<Question> findByTopicId(Long topicId) {
        String sql = """
            SELECT id, user_id, topic_id, text_question, source, language
            FROM questions
            WHERE topic_id = ?
            """;

        List<Question> questions = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setLong(1, topicId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    questions.add(mapResultSetToQuestion(resultSet));
                }
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска вопросов по id темы.", e);
        }
    }

    @Override
    public List<Question> findByUserId(Long userId) {
        String sql = """
            SELECT id, user_id, topic_id, text_question, source, language
            FROM questions
            WHERE user_id = ?
            """;

        List<Question> questions = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    questions.add(mapResultSetToQuestion(resultSet));
                }
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске вопросов по id пользователя.", e);
        }
    }

    @Override
    public List<Question> findAll(){
        String sql = """
        SELECT id, user_id, topic_id, text_question, source, language
        FROM questions
        """;
        List<Question> questions = new ArrayList<>();
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    questions.add(mapResultSetToQuestion(resultSet));
                }
            }
            return questions;

        }catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске всех вопросов.", e);
        }
    }

    @Override
    public Question update(Question question){
        String sql = """
                UPDATE questions
                SET text_question = ?, source = ?, language = ?
                WHERE id = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ){
            preparedStatement.setString(1, question.getTextQuestion());
            if (question.getSource() == null || question.getSource().isBlank()) {
                preparedStatement.setString(2, "manual");
            } else {
                preparedStatement.setString(2, question.getSource());
            }

            if (question.getLanguage() == null || question.getLanguage().isBlank()) {
                preparedStatement.setString(3, "ru");
            } else {
                preparedStatement.setString(3, question.getLanguage());
            }
            preparedStatement.setLong(4, question.getId());

            int rowsUpdate = preparedStatement.executeUpdate();

            if(rowsUpdate == 0){
                throw new RuntimeException("Вопрос с id = " + question.getId() + " не найден.");
            }

            return question;

        }catch (SQLException e){
            throw new RuntimeException("Ошибка при обновлении вопроса.", e);
        }
    }

    @Override
    public void deleteById(Long id){
        String sql = """
                DELETE FROM questions
                WHERE id = ?
                """;
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);){

            preparedStatement.setLong(1, id);

            int rowsDelete = preparedStatement.executeUpdate();

            if(rowsDelete == 0){
                throw new RuntimeException("Вопрос с id = " + id + " не найден.");
            }
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при удалении вопроса по id.", e);
        }
    }

}














