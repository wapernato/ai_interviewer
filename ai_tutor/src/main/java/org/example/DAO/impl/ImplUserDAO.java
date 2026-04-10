package org.example.DAO.impl;

import org.example.DAO.UserDAO;
import org.example.DBConnection.DBConnection;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImplUserDAO implements UserDAO {

    private final DBConnection dbConnection;

    public ImplUserDAO(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public User save(User user){
        String sql = "insert into users(username) values (?) returning id";
        try ( Connection connection = dbConnection.getConnectionDB();
              PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, user.getUsername());
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()){
                    Long generatedId = resultSet.getLong("id");
                    user.setId(generatedId);
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    @Override
    public User findByName(String username){
        String sql = "select id, username from users where username = ?";
        try (Connection connection = dbConnection.getConnectionDB();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setUsername(resultSet.getString("username"));
                    return user;
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске пользователя по username",e);
        }
        return null;
    }

    @Override
    public User findById(Long id){
        String sql = "select id, username from users where id = ?";
        try (Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setUsername(resultSet.getString("username"));
                    return user;
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске пользователя по id", e);
        }
        return null;
    }

    @Override
    public List<User> findAll(){
        String sql = "select id, username from users";
        List<User> users = new ArrayList<>();
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ){
            try(ResultSet resultSet = preparedStatement.executeQuery()) {

                while(resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setUsername(resultSet.getString("username"));
                    users.add(user);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при получении всех пользователей ", e);
        }
        return users;
    }

    @Override
    public User update(User user){
        String sql = "update users set username = ? where id = ?";
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setLong(2, user.getId());

            int rowsUpdate = preparedStatement.executeUpdate();

            if(rowsUpdate == 0){
                throw new RuntimeException("Пользователь с id = " + user.getId() + " не найден");
            }

            return user;

        }catch (SQLException e){
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void deleteById(Long id){
        String sql = "delete from users where id = ?";
        try(Connection connection = dbConnection.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setLong(1, id);
            int rowsDelete = preparedStatement.executeUpdate();

            if(rowsDelete == 0){
                throw new RuntimeException("Пользователь с таким id = " + id + " не найден");
            }
        }catch (SQLException e){
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }

}
