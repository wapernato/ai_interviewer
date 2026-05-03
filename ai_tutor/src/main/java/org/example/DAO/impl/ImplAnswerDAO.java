package org.example.DAO.impl;

import org.example.DAO.AnswerDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.Answer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
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

    public Answer update(Long id){
        String sql = """
                UPDATE answers
                SET answer_text, model_name
                WHERE ID = ?
                """;
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setLong(1, id);
            int updateRows = preparedStatement.executeUpdate();

            if(updateRows == 0){
                throw new RuntimeException("Строки не были обновлены.");
            }

            
        }
        catch (SQLException e){
            throw new RuntimeException("Ошибка при обновлении ответа.", e);
        }
    }
}
