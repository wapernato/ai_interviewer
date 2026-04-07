package org.example.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final String USER = "postgres";
    private final String PASSWORD = "2773";

    public Connection getConnectionDB() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
