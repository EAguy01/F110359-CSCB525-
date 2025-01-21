package com.homemanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//Осигурява ефективно използване на ресурсите чрез поддържане на един екземпляр на връзката.
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/electronic_home_manager?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException("Error connecting to the database", e);
            }
        }
        return connection;
    }
}
