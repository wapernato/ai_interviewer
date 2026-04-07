package org.example.DAO.impl;

import org.example.DAO.UserRegestration;
import org.example.DBConnection.DBConnection;
import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImplUserRegestration implements UserRegestration {

    private final DBConnection dbConnection;

    public ImplUserRegestration(DBConnection dbConnection) {
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
            throw new RuntimeException(e);
        }
    }

}
