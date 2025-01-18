package org.example.database;

import org.example.service.DataLocalSQL;
import org.example.models.PrivateChat;
import org.example.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.example.service.DatabaseConnection;

public class PrivateChatTable extends DataLocalSQL<PrivateChat> {

  private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS private_chats (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user1_id INTEGER NOT NULL,
            user2_id INTEGER NOT NULL,
            createdAt TEXT NOT NULL,
            FOREIGN KEY (user1_id) REFERENCES users(id),
            FOREIGN KEY (user2_id) REFERENCES users(id)
        )
    """;

  private static final String INSERT_SQL = """
        INSERT INTO private_chats (user1_id, user2_id, createdAt)
        VALUES (?, ?, ?)
    """;

  private static final String FIND_BY_ID_SQL = """
        SELECT * FROM private_chats WHERE id = ?
    """;

  private static final String UPDATE_SQL = """
        UPDATE private_chats SET user1_id = ?, user2_id = ?, createdAt = ? WHERE id = ?
    """;

  private static final String DELETE_SQL = """
        DELETE FROM private_chats WHERE id = ?
    """;

  public PrivateChatTable() {
    createTable(CREATE_TABLE_SQL);
  }

  @Override
  public void create(PrivateChat chat) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

      statement.setInt(1, chat.getUser1().getId());
      statement.setInt(2, chat.getUser2().getId());
      statement.setString(3, chat.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));

      statement.executeUpdate();
      System.out.println("Private chat created successfully.");
    } catch (SQLException e) {
      System.out.println("Error creating private chat: " + e.getMessage());
    }
  }

  @Override
  public PrivateChat findById(int id) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return mapResultSetToEntity(resultSet);
      }
    } catch (SQLException e) {
      System.out.println("Error finding private chat: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void update(PrivateChat chat) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

      statement.setInt(1, chat.getUser1().getId());
      statement.setInt(2, chat.getUser2().getId());
      statement.setString(3, chat.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
      statement.setInt(4, chat.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Private chat updated successfully.");
      } else {
        System.out.println("No private chat found with the specified ID.");
      }
    } catch (SQLException e) {
      System.out.println("Error updating private chat: " + e.getMessage());
    }
  }

  @Override
  protected PrivateChat mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    int user1Id = resultSet.getInt("user1_id");
    int user2Id = resultSet.getInt("user2_id");
    LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("createdAt"), DateTimeFormatter.ISO_DATE_TIME);

    User user1 = new UserTable().findById(user1Id);
    User user2 = new UserTable().findById(user2Id);

    return new PrivateChat(id, user1, user2, createdAt);
  }
}
