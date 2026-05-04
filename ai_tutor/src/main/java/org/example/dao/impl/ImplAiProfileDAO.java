package org.example.dao.impl;

import org.example.dao.AiProfileDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.AiProfile;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImplAiProfileDAO implements AiProfileDAO {

    private final DBConnection dbConnection;

    public ImplAiProfileDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    private AiProfile mapResultSetToAiProfile(ResultSet resultSet) throws SQLException {
        AiProfile aiProfile = new AiProfile();

        aiProfile.setId(resultSet.getLong("id"));
        aiProfile.setMode(resultSet.getString("mode"));
        aiProfile.setDescriptionMode(resultSet.getString("description_mode"));
        aiProfile.setInstructionMode(resultSet.getString("instruction_mode"));

        aiProfile.setModelName(resultSet.getString("model_name"));
        aiProfile.setLanguage(resultSet.getString("language"));
        aiProfile.setAnswerStyle(resultSet.getString("answer_style"));

        aiProfile.setDifficulty(resultSet.getString("difficulty"));
        aiProfile.setFeedbackMode(resultSet.getString("feedback_mode"));

        aiProfile.setHintMode(resultSet.getBoolean("hint_mode"));
        aiProfile.setActive(resultSet.getBoolean("active"));

        BigDecimal temperature = resultSet.getBigDecimal("temperature");
        aiProfile.setTemperature(temperature == null ? null : temperature.doubleValue());

        int maxTokens = resultSet.getInt("max_tokens");
        aiProfile.setMaxTokens(resultSet.wasNull() ? null : maxTokens);

        return aiProfile;
    }

    @Override
    public AiProfile save(AiProfile aiProfile) {
        String sql = """
                insert into ai_profiles(
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                returning id
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, aiProfile.getMode());
            preparedStatement.setString(2, aiProfile.getDescriptionMode());
            preparedStatement.setString(3, aiProfile.getInstructionMode());
            preparedStatement.setString(4, aiProfile.getModelName());
            preparedStatement.setString(5, aiProfile.getLanguage());
            preparedStatement.setString(6, aiProfile.getAnswerStyle());
            preparedStatement.setString(7, aiProfile.getDifficulty());
            preparedStatement.setString(8, aiProfile.getFeedbackMode());
            preparedStatement.setBoolean(9, aiProfile.getHintMode());
            preparedStatement.setBoolean(10, aiProfile.getActive());

            if (aiProfile.getTemperature() == null) {
                preparedStatement.setNull(11, Types.NUMERIC);
            } else {
                preparedStatement.setBigDecimal(11, BigDecimal.valueOf(aiProfile.getTemperature()));
            }

            if (aiProfile.getMaxTokens() == null) {
                preparedStatement.setNull(12, Types.INTEGER);
            } else {
                preparedStatement.setInt(12, aiProfile.getMaxTokens());
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    aiProfile.setId(resultSet.getLong("id"));
                }
            }

            return aiProfile;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении AI-профиля.", e);
        }
    }

    @Override
    public AiProfile findById(Long id) {
        String sql = """
                select
                    id,
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                from ai_profiles
                where id = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAiProfile(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске AI-профиля по id.", e);
        }
    }

    @Override
    public AiProfile findByMode(String mode) {
        String sql = """
                select
                    id,
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                from ai_profiles
                where mode = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, mode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAiProfile(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске AI-профиля по mode.", e);
        }
    }

    @Override
    public List<AiProfile> findAll() {
        String sql = """
                select
                    id,
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                from ai_profiles
                order by id
                """;

        List<AiProfile> profiles = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                profiles.add(mapResultSetToAiProfile(resultSet));
            }

            return profiles;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка AI-профилей.", e);
        }
    }

    @Override
    public AiProfile update(AiProfile aiProfile) {
        String sql = """
                update ai_profiles
                set
                    mode = ?,
                    description_mode = ?,
                    instruction_mode = ?,
                    model_name = ?,
                    language = ?,
                    answer_style = ?,
                    difficulty = ?,
                    feedback_mode = ?,
                    hint_mode = ?,
                    active = ?,
                    temperature = ?,
                    max_tokens = ?
                where id = ?
                returning
                    id,
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, aiProfile.getMode());
            preparedStatement.setString(2, aiProfile.getDescriptionMode());
            preparedStatement.setString(3, aiProfile.getInstructionMode());
            preparedStatement.setString(4, aiProfile.getModelName());
            preparedStatement.setString(5, aiProfile.getLanguage());
            preparedStatement.setString(6, aiProfile.getAnswerStyle());
            preparedStatement.setString(7, aiProfile.getDifficulty());
            preparedStatement.setString(8, aiProfile.getFeedbackMode());
            preparedStatement.setBoolean(9, aiProfile.getHintMode());
            preparedStatement.setBoolean(10, aiProfile.getActive());

            if (aiProfile.getTemperature() == null) {
                preparedStatement.setNull(11, Types.NUMERIC);
            } else {
                preparedStatement.setBigDecimal(11, BigDecimal.valueOf(aiProfile.getTemperature()));
            }

            if (aiProfile.getMaxTokens() == null) {
                preparedStatement.setNull(12, Types.INTEGER);
            } else {
                preparedStatement.setInt(12, aiProfile.getMaxTokens());
            }

            preparedStatement.setLong(13, aiProfile.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAiProfile(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении AI-профиля.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                delete from ai_profiles
                where id = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении AI-профиля.", e);
        }
    }

    @Override
    public AiProfile findActive() {
        String sql = """
                select
                    id,
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                from ai_profiles
                where active = true
                limit 1
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return mapResultSetToAiProfile(resultSet);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске активного AI-профиля.", e);
        }
    }

    @Override
    public void deactivateAll() {
        String sql = """
                update ai_profiles
                set active = false
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при деактивации AI-профилей.", e);
        }
    }

    @Override
    public AiProfile activateById(Long id) {
        String sql = """
                update ai_profiles
                set active = true
                where id = ?
                returning
                    id,
                    mode,
                    description_mode,
                    instruction_mode,
                    model_name,
                    language,
                    answer_style,
                    difficulty,
                    feedback_mode,
                    hint_mode,
                    active,
                    temperature,
                    max_tokens
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAiProfile(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при активации AI-профиля.", e);
        }
    }
}