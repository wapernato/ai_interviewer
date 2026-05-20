package org.example.DBConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DBConnection {

    private final String url;
    private final String username;
    private final String password;

    public DBConnection(@Value("${spring.datasource.url}") String url,
                        @Value("${spring.datasource.username}") String username,
                        @Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnectionDB() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
