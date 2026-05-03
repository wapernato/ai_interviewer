package org.example.DAO.impl;

import org.example.DAO.AnswerDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.Answer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ImplAnswerDAO implements AnswerDAO {

    private final DBConnection dbConnection;

    public ImplAnswerDAO(DBConnection dbConnection) { this.dbConnection = dbConnection; }

    private Answer mapResultSetToAnswer(ResultSet resultSet) throws SQLException {
        Answer answer = new Answer();

        answer.setId(resultSet.getLong("id"));
        answer.setAiProfileId(resultSet.getObject("ai_profile_id", Long.class));
        answer.setQuestionId(resultSet.getLong("question_id"));
        answer.setAnswerText(resultSet.getString("answer_text"));
        answer.setModelName(resultSet.getString("model_name"));

        return answer;
    }

    @Override
    public Answer save(Answer answer){
        String sql = """
                INSERT INTO answers(question_id, ai_profile_id, answer_text, model_name)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            if (answer == null) {
                throw new RuntimeException("Ответ не может быть null.");
            }

            if (answer.getQuestionId() == null) {
                throw new RuntimeException("Id вопроса не может быть null.");
            }

            if (answer.getAnswerText() == null || answer.getAnswerText().isBlank()) {
                throw new RuntimeException("Текст ответа не может быть пустым.");
            }

            if (answer.getModelName() == null || answer.getModelName().isBlank()) {
                throw new RuntimeException("Название модели не может быть пустым.");
            }

            preparedStatement.setLong(1, answer.getQuestionId());
            if (answer.getAiProfileId() != null) {
                preparedStatement.setLong(2, answer.getAiProfileId());
            } else {
                preparedStatement.setNull(2, java.sql.Types.BIGINT);
            }
            preparedStatement.setString(3, answer.getAnswerText());
            preparedStatement.setString(4, answer.getModelName());

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    Long generatedId = resultSet.getLong("id");
                    answer.setId(generatedId);
                    return answer;
                }
            }

            throw new RuntimeException("Ответ не был сохранён.");

        }catch (SQLException e){
            throw new RuntimeException("Ошибка при сохранении ответа.", e);
        }
    }

    @Override
    public Answer update(Answer answer){
        String sql = """
                UPDATE answers
                SET answer_text = ?, model_name = ?
                WHERE id = ?
                """;
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            if(answer.getAnswerText() == null|| answer.getAnswerText().isBlank()){
                throw new RuntimeException("Текст ответа не может быть пустым.");
            }else{
                preparedStatement.setString(1, answer.getAnswerText());
            }
            if(answer.getModelName() == null|| answer.getModelName().isBlank()){
                throw new RuntimeException("Название модели не может быть пустым.");
            }else{
                preparedStatement.setString(2, answer.getModelName());
            }

            if(answer.getId() == null){
                throw new RuntimeException("Id ответа не может быть null.");
            }
            preparedStatement.setLong(3, answer.getId());

            int updateRows = preparedStatement.executeUpdate();

            if(updateRows == 0){
                throw new RuntimeException("Такого ответа не было найдено.");
            }

            return answer;
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при обновлении ответа.", e);
        }
    }

    @Override
    public void deleteById(Long id){
        String sql = """
                DELETE from answers
                WHERE id = ?
                """;
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ){
            if(id == null){
                throw new RuntimeException("Id ответа не может быть null.");
            }
            preparedStatement.setLong(1, id);

            int deleteRows = preparedStatement.executeUpdate();

            if(deleteRows == 0){
                throw new RuntimeException("Ответ с таким id не найден.");
            }

        }catch (SQLException e){
            throw new RuntimeException("Ошибка при удалении вопроса по id", e);
        }
    }

    @Override
    public List<Answer> findByQuestionId(Long questionId){
        String sql = """
                SELECT id, question_id, ai_profile_id, answer_text, model_name
                FROM answers
                WHERE question_id = ?
                """;
        List<Answer> answers = new ArrayList<>();
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            if (questionId == null) {
                throw new RuntimeException("Id вопроса не может быть null.");
            }
            preparedStatement.setLong(1, questionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    answers.add(mapResultSetToAnswer(resultSet));
                }
                return answers;
            }
        }
        catch(SQLException e){
            throw new RuntimeException("Ошибка при поиске ответа по id вопроса", e);
        }
    }

    @Override
    public List<Answer> findByProfileId(Long profileId) {
        String sql = """
            SELECT id, question_id, ai_profile_id, answer_text, model_name
            FROM answers
            WHERE ai_profile_id = ?
            """;

        List<Answer> answers = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (profileId == null) {
                throw new RuntimeException("Id AI-профиля не может быть null.");
            }

            preparedStatement.setLong(1, profileId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    answers.add(mapResultSetToAnswer(resultSet));
                }
            }

            return answers;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске ответов по id AI-профиля.", e);
        }
    }

    @Override
    public Answer findById(Long id) {
        String sql = """
            SELECT id, question_id, ai_profile_id, answer_text, model_name
            FROM answers
            WHERE id = ?
            """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (id == null) {
                throw new RuntimeException("Id ответа не может быть null.");
            }

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAnswer(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске ответа по id.", e);
        }
    }

    @Override
    public List<Answer> findAll() {
        String sql = """
            SELECT id, question_id, ai_profile_id, answer_text, model_name
            FROM answers
            """;

        List<Answer> answers = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                answers.add(mapResultSetToAnswer(resultSet));
            }

            return answers;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении всех ответов.", e);
        }
    }
}
