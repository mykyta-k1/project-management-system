package org.example.database;

import org.example.models.NotificationImpl;
import org.example.service.DataLocalSQL;
import org.example.service.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationsTable extends DataLocalSQL<NotificationImpl> {

  private static final String CREATE_NOTIFICATIONS_TABLE = """
    CREATE TABLE IF NOT EXISTS notifications (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER NOT NULL,
        chat_id INTEGER NOT NULL,
        message_id INTEGER NOT NULL,
        is_read BOOLEAN NOT NULL DEFAULT 0,
        created_at TEXT NOT NULL,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (chat_id) REFERENCES private_chats(id) ON DELETE CASCADE,
        FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
    );
""";

  private static final String CREATE_INDEXES = """
    CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notifications(user_id);
    CREATE INDEX IF NOT EXISTS idx_notification_chat_id ON notifications(chat_id);
""";

  private static final String INSERT_NOTIFICATION_SQL = """
        INSERT INTO notifications (user_id, chat_id, message_id, is_read, created_at)
        VALUES (?, ?, ?, ?, ?);
    """;

  private static final String FIND_BY_ID_SQL = """
        SELECT * FROM notifications WHERE id = ?;
    """;

  private static final String FIND_BY_USER_SQL = """
        SELECT * FROM notifications WHERE user_id = ?;
    """;

  private static final String UPDATE_NOTIFICATION_SQL = """
        UPDATE notifications SET is_read = ? WHERE id = ?;
    """;

  private static final String DELETE_NOTIFICATION_SQL = """
        DELETE FROM notifications WHERE id = ?;
    """;

  public NotificationsTable() {
    createTable(CREATE_NOTIFICATIONS_TABLE);
    createTable(CREATE_INDEXES);
  }

  @Override
  public void create(NotificationImpl notification) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_NOTIFICATION_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, notification.getUserId());
      statement.setInt(2, notification.getChatId());
      statement.setInt(3, notification.getMessageId());
      statement.setBoolean(4, notification.isRead());
      statement.setString(5, notification.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));

      statement.executeUpdate();
      System.out.println("Notification created successfully.");
    } catch (SQLException e) {
      System.out.println("Error creating notification: " + e.getMessage());
    }
  }

  @Override
  public NotificationImpl findById(int id) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return mapResultSetToEntity(resultSet);
      }
    } catch (SQLException e) {
      System.out.println("Error finding notification by ID: " + e.getMessage());
    }
    return null;
  }

  public List<NotificationImpl> findByUserId(int userId) {
    List<NotificationImpl> notifications = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, userId);
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        notifications.add(mapResultSetToEntity(resultSet));
      }
    } catch (SQLException e) {
      System.out.println("Error finding notifications by user ID: " + e.getMessage());
    }
    return notifications;
  }

  @Override
  public void update(NotificationImpl notification) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_NOTIFICATION_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setBoolean(1, notification.isRead());
      statement.setInt(2, notification.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Notification updated successfully.");
      } else {
        System.out.println("No notification found with the specified ID.");
      }
    } catch (SQLException e) {
      System.out.println("Error updating notification: " + e.getMessage());
    }
  }

  @Override
  protected NotificationImpl mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    int userId = resultSet.getInt("user_id");
    int chatId = resultSet.getInt("chat_id");
    int messageId = resultSet.getInt("message_id");
    boolean isRead = resultSet.getBoolean("is_read");
    LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("created_at"), DateTimeFormatter.ISO_DATE_TIME);

    return new NotificationImpl(id, userId, chatId, messageId, isRead, createdAt);
  }

  public void deleteNotification(int id) {
    deleteById(DELETE_NOTIFICATION_SQL, id);
  }
}
