package org.example.dao.impl;

import org.example.dao.AiProfileDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.AiProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        aiProfile.setLanguage(resultSet.getString("language"));
        aiProfile.setAnswerStyle(resultSet.getString("answer_style"));
        aiProfile.setHintMode(resultSet.getObject("hint_mode", Boolean.class));
        aiProfile.setActive(resultSet.getObject("active", Boolean.class));

        return aiProfile;
    }

    @Override
    public AiProfile save(AiProfile aiProfile) {
        String sql = """
                INSERT INTO ai_profiles(
                    mode,
                    description_mode,
                    instruction_mode,
                    language,
                    answer_style,
                    hint_mode,
                    active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (aiProfile == null) {
                throw new RuntimeException("AI-профиль не может быть null.");
            }

            if (aiProfile.getMode() == null || aiProfile.getMode().isBlank()) {
                throw new RuntimeException("Режим AI-профиля не может быть пустым.");
            }

            if (aiProfile.getInstructionMode() == null || aiProfile.getInstructionMode().isBlank()) {
                throw new RuntimeException("Инструкция AI-профиля не может быть пустой.");
            }

            String language = aiProfile.getLanguage();
            if (language == null || language.isBlank()) {
                language = "ru";
            }

            Boolean hintMode = aiProfile.getHintMode();
            if (hintMode == null) {
                hintMode = false;
            }

            Boolean active = aiProfile.getActive();
            if (active == null) {
                active = false;
            }

            preparedStatement.setString(1, aiProfile.getMode().trim());
            preparedStatement.setString(2, aiProfile.getDescriptionMode());
            preparedStatement.setString(3, aiProfile.getInstructionMode().trim());
            preparedStatement.setString(4, language.trim());
            preparedStatement.setString(5, aiProfile.getAnswerStyle());
            preparedStatement.setBoolean(6, hintMode);
            preparedStatement.setBoolean(7, active);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong("id");
                    aiProfile.setId(generatedId);
                    aiProfile.setLanguage(language);
                    aiProfile.setHintMode(hintMode);
                    aiProfile.setActive(active);
                    return aiProfile;
                }
            }

            throw new RuntimeException("AI-профиль не был сохранён.");

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении AI-профиля.", e);
        }
    }

    @Override
    public AiProfile update(AiProfile aiProfile) {
        String sql = """
                UPDATE ai_profiles
                SET mode = ?,
                    description_mode = ?,
                    instruction_mode = ?,
                    language = ?,
                    answer_style = ?,
                    hint_mode = ?,
                    active = ?
                WHERE id = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (aiProfile == null) {
                throw new RuntimeException("AI-профиль не может быть null.");
            }

            if (aiProfile.getId() == null) {
                throw new RuntimeException("Id AI-профиля не может быть null.");
            }

            if (aiProfile.getMode() == null || aiProfile.getMode().isBlank()) {
                throw new RuntimeException("Режим AI-профиля не может быть пустым.");
            }

            if (aiProfile.getInstructionMode() == null || aiProfile.getInstructionMode().isBlank()) {
                throw new RuntimeException("Инструкция AI-профиля не может быть пустой.");
            }

            String language = aiProfile.getLanguage();
            if (language == null || language.isBlank()) {
                language = "ru";
            }

            Boolean hintMode = aiProfile.getHintMode();
            if (hintMode == null) {
                hintMode = false;
            }

            Boolean active = aiProfile.getActive();
            if (active == null) {
                active = false;
            }

            preparedStatement.setString(1, aiProfile.getMode().trim());
            preparedStatement.setString(2, aiProfile.getDescriptionMode());
            preparedStatement.setString(3, aiProfile.getInstructionMode().trim());
            preparedStatement.setString(4, language.trim());
            preparedStatement.setString(5, aiProfile.getAnswerStyle());
            preparedStatement.setBoolean(6, hintMode);
            preparedStatement.setBoolean(7, active);
            preparedStatement.setLong(8, aiProfile.getId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new RuntimeException("AI-профиль с таким id не найден.");
            }

            aiProfile.setLanguage(language);
            aiProfile.setHintMode(hintMode);
            aiProfile.setActive(active);

            return aiProfile;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении AI-профиля.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                DELETE FROM ai_profiles
                WHERE id = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (id == null) {
                throw new RuntimeException("Id AI-профиля не может быть null.");
            }

            preparedStatement.setLong(1, id);

            int deletedRows = preparedStatement.executeUpdate();

            if (deletedRows == 0) {
                throw new RuntimeException("AI-профиль с таким id не найден.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении AI-профиля по id.", e);
        }
    }

    @Override
    public AiProfile findById(Long id) {
        String sql = """
                SELECT id, mode, description_mode, instruction_mode, language, answer_style, hint_mode, active
                FROM ai_profiles
                WHERE id = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (id == null) {
                throw new RuntimeException("Id AI-профиля не может быть null.");
            }

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
                SELECT id, mode, description_mode, instruction_mode, language, answer_style, hint_mode, active
                FROM ai_profiles
                WHERE mode = ?
                """;

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (mode == null || mode.isBlank()) {
                throw new RuntimeException("Режим AI-профиля не может быть пустым.");
            }

            preparedStatement.setString(1, mode.trim());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAiProfile(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске AI-профиля по режиму.", e);
        }
    }

    @Override
    public List<AiProfile> findAll() {
        String sql = """
                SELECT id, mode, description_mode, instruction_mode, language, answer_style, hint_mode, active
                FROM ai_profiles
                """;

        List<AiProfile> aiProfiles = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                aiProfiles.add(mapResultSetToAiProfile(resultSet));
            }

            return aiProfiles;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении всех AI-профилей.", e);
        }
    }

    @Override
    public List<AiProfile> findActiveProfiles() {
        String sql = """
                SELECT id, mode, description_mode, instruction_mode, language, answer_style, hint_mode, active
                FROM ai_profiles
                WHERE active = true
                """;

        List<AiProfile> aiProfiles = new ArrayList<>();

        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                aiProfiles.add(mapResultSetToAiProfile(resultSet));
            }

            return aiProfiles;

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении активных AI-профилей.", e);
        }
    }
}