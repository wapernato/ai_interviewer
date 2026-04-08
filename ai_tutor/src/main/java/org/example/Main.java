package org.example;

import org.example.DAO.UserRegestration;
import org.example.DAO.impl.ImplUserRegestration;
import org.example.DBConnection.DBConnection;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.impl.ImplUserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        DBConnection dbConnection = new DBConnection();
        UserRegestration userRegestration = new ImplUserRegestration(dbConnection);
        UserService userService = new ImplUserService(userRegestration);

        System.out.print("Введите имя пользователя: ");
        String username = reader.readLine();
        User user = userService.register(username);
        System.out.println("Имя - " + user.getUsername());
        System.out.println("ID - " + user.getId());

    }
}