package org.example.database;

import java.time.LocalDateTime;
import org.example.models.User;
import org.example.service.DataLocalSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.service.DatabaseConnection;

public class PrivateChatsUsersTable extends DataLocalSQL<User> {

  private static final String CREATE_PRIVATE_CHATS_USERS_TABLE = """
    CREATE TABLE IF NOT EXISTS private_chat_users (
        chat_id INTEGER NOT NULL,
        user_id INTEGER NOT NULL,
        PRIMARY KEY (chat_id, user_id),
        FOREIGN KEY (chat_id) REFERENCES private_chats(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
""";

  private static final String CREATE_INDEXES = """
    CREATE INDEX IF NOT EXISTS idx_private_chat_id ON private_chat_users(chat_id);
    CREATE INDEX IF NOT EXISTS idx_private_user_id ON private_chat_users(user_id);
""";

  private static final String INSERT_SQL = """
        INSERT INTO private_chat_users (chat_id, user_id)
        VALUES (?, ?);
    """;

  private static final String FIND_USERS_BY_CHAT_ID_SQL = """
        SELECT u.id, u.username, u.email, u.password, u.avatarUrl, u.lastActive
        FROM private_chat_users pc
        JOIN users u ON pc.user_id = u.id
        WHERE pc.chat_id = ?;
    """;

  private static final String DELETE_BY_CHAT_ID_SQL = """
        DELETE FROM private_chat_users WHERE chat_id = ?;
    """;

  private static final String DELETE_BY_USER_ID_SQL = """
        DELETE FROM private_chat_users WHERE user_id = ?;
    """;

  public PrivateChatsUsersTable() {
    createTable(CREATE_PRIVATE_CHATS_USERS_TABLE);
    createTable(CREATE_INDEXES);
  }

  @Override
  public void create(User entity) {
    throw new UnsupportedOperationException("This method is not supported for PrivateChatsUsersTable. Use create(User user, int chatId) instead.");
  }

  public void create(User user, int chatId) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, chatId);
      statement.setInt(2, user.getId());
      statement.executeUpdate();

      System.out.println("User added to chat successfully.");
    } catch (SQLException e) {
      System.out.println("Error adding user to chat: " + e.getMessage());
    }
  }

  public List<User> findUsersByChatId(int chatId) {
    List<User> users = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_USERS_BY_CHAT_ID_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, chatId);
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        users.add(mapResultSetToEntity(resultSet));
      }
    } catch (SQLException e) {
      System.out.println("Error finding users by chat ID: " + e.getMessage());
    }
    return users;
  }

  public void deleteByChatId(int chatId) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE_BY_CHAT_ID_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, chatId);
      int rowsAffected = statement.executeUpdate();
      System.out.println(rowsAffected + " rows deleted for chat ID " + chatId);
    } catch (SQLException e) {
      System.out.println("Error deleting users by chat ID: " + e.getMessage());
    }
  }

  public void deleteByUserId(int userId) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE_BY_USER_ID_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, userId);
      int rowsAffected = statement.executeUpdate();
      System.out.println(rowsAffected + " rows deleted for user ID " + userId);
    } catch (SQLException e) {
      System.out.println("Error deleting chats by user ID: " + e.getMessage());
    }
  }

  @Override
  public User findById(int id) {
    throw new UnsupportedOperationException("This operation is not supported for this table.");
  }

  @Override
  public void update(User entity) {
    throw new UnsupportedOperationException("This operation is not supported for this table.");
  }

  @Override
  protected User mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String username = resultSet.getString("username");
    String email = resultSet.getString("email");
    String password = resultSet.getString("password");
    String avatarUrl = resultSet.getString("avatarUrl");
    LocalDateTime lastActive = resultSet.getTimestamp("lastActive").toLocalDateTime();
    User user = new User(id, username, email, password, avatarUrl);
    user.setLastActive(lastActive);
    return user;
  }
}
