package org.example.database;

import org.example.service.DataLocalSQL;
import org.example.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.service.DatabaseConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDatabase extends DataLocalSQL<User> {
  private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            email TEXT NOT NULL,
            password TEXT NOT NULL,
            avatarUrl TEXT DEFAULT '/Images/default_avatar.png',
            lastActive TEXT
        );
    """;

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public UserDatabase() {
    createTable(CREATE_TABLE_SQL);
  }

  @Override
  public void create(User user) {
    String insertSQL = "INSERT INTO users (username, email, password, avatarUrl, lastActive) VALUES (?, ?, ?, ?, ?)";
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(insertSQL)) {

      statement.setString(1, user.getUsername());
      statement.setString(2, user.getEmail());
      statement.setString(3, user.getPassword());
      statement.setString(4, user.getAvatarUrl());
      statement.setString(5, user.getLastActive() != null ? user.getLastActive().format(FORMATTER) : null);
      statement.executeUpdate();

      System.out.println("User added successfully.");
    } catch (SQLException e) {
      System.out.println("Error creating user: " + e.getMessage());
    }
  }

  @Override
  public User findById(int id) {
    String selectSQL = "SELECT * FROM users WHERE id = ?";
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(selectSQL)) {

      statement.setInt(1, id);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToEntity(resultSet);
        }
      }
    } catch (SQLException e) {
      System.out.println("Error finding user: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void update(User user) {
    String updateSQL = "UPDATE users SET username = ?, email = ?, password = ?, avatarUrl = ?, lastActive = ? WHERE id = ?";
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(updateSQL)) {

      statement.setString(1, user.getUsername());
      statement.setString(2, user.getEmail());
      statement.setString(3, user.getPassword());
      statement.setString(4, user.getAvatarUrl());
      statement.setString(5, user.getLastActive() != null ? user.getLastActive().format(FORMATTER) : null);
      statement.setInt(6, user.getId());
      int rowsAffected = statement.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("User updated successfully.");
      } else {
        System.out.println("No user found with the specified ID.");
      }
    } catch (SQLException e) {
      System.out.println("Error updating user: " + e.getMessage());
    }
  }

  @Override
  protected User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String username = resultSet.getString("username");
    String email = resultSet.getString("email");
    String password = resultSet.getString("password");
    String avatarUrl = resultSet.getString("avatarUrl");
    String lastActiveString = resultSet.getString("lastActive");

    User user = new User(id, username, email, password, avatarUrl);
    if (lastActiveString != null) {
      user.setLastActive(LocalDateTime.parse(lastActiveString, FORMATTER));
    }
    return user;
  }
}

