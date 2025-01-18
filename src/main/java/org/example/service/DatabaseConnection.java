package org.example.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/messenger_local_db.db";
  private static DatabaseConnection instance;
  private Connection connection;

  private DatabaseConnection() {
    try {
      this.connection = DriverManager.getConnection(DATABASE_URL);
      System.out.println("Connection to SQLite has been established.");
    } catch (SQLException e) {
      System.out.println("Error connecting to SQLite: " + e.getMessage());
    }
  }

  public static synchronized DatabaseConnection getInstance() {
    if (instance == null) {
      instance = new DatabaseConnection();
    }
    return instance;
  }

  public Connection getConnection() {
    if (connection == null) {
      try {
        connection = DriverManager.getConnection(DATABASE_URL);
      } catch (SQLException e) {
        System.out.println("Error reconnecting to SQLite: " + e.getMessage());
      }
    }
    return connection;
  }

  public void closeConnection() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        System.out.println("Connection to SQLite has been closed.");
      }
    } catch (SQLException e) {
      System.out.println("Error closing SQLite connection: " + e.getMessage());
    }
  }
}

