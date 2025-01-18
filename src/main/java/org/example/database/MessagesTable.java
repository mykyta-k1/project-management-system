package org.example.database;

import org.example.models.Message;
import org.example.models.PrivateChat;
import org.example.models.User;
import org.example.service.DataLocalSQL;
import org.example.service.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessagesTable extends DataLocalSQL<Message> {

  private static final String CREATE_MESSAGES_TABLE = """
    CREATE TABLE IF NOT EXISTS messages (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        chat_id INTEGER NOT NULL,
        user_id INTEGER NOT NULL,
        text_content TEXT,
        image_url TEXT,
        video_url TEXT,
        document_url TEXT,
        isRead BOOLEAN DEFAULT 0,
        sentAt TEXT NOT NULL,
        FOREIGN KEY (chat_id) REFERENCES private_chats(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
""";

  private static final String CREATE_INDEXES = """
    CREATE INDEX IF NOT EXISTS idx_message_chat_id ON messages(chat_id);
    CREATE INDEX IF NOT EXISTS idx_message_user_id ON messages(user_id);
    CREATE INDEX IF NOT EXISTS idx_message_sentAt ON messages(sentAt);
    CREATE INDEX IF NOT EXISTS idx_message_isRead ON messages(isRead);
""";

  private static final String INSERT_MESSAGE_SQL = """
        INSERT INTO messages (chat_id, user_id, text_content, image_url, video_url, document_url, isRead, sentAt)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

  private static final String FIND_BY_ID_SQL = "SELECT * FROM messages WHERE id = ?";

  private static final String FIND_ALL_BY_CHAT_SQL = "SELECT * FROM messages WHERE chat_id = ? ORDER BY sentAt ASC";

  private static final String UPDATE_MESSAGE_SQL = """
        UPDATE messages
        SET text_content = ?, image_url = ?, video_url = ?, document_url = ?, isRead = ?
        WHERE id = ?""";

  private static final String DELETE_MESSAGE_SQL = "DELETE FROM messages WHERE id = ?";

  public MessagesTable() {
    createTable(CREATE_MESSAGES_TABLE);
    createTable(CREATE_INDEXES);
  }

  @Override
  public void create(Message message) {
    if (message.getPrivateChat() == null || message.getUser() == null || message.getSentAt() == null) {
      throw new IllegalArgumentException("Missing required fields for creating a message.");
    }
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_MESSAGE_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, message.getPrivateChat().getId());
      statement.setInt(2, message.getUser().getId());
      statement.setString(3, message.getTextContent());
      statement.setString(4, message.getImageUrl());
      statement.setString(5, message.getVideoUrl());
      statement.setString(6, message.getDocumentUrl());
      statement.setBoolean(7, message.isRead());
      statement.setString(8, message.getSentAt().format(DateTimeFormatter.ISO_DATE_TIME));

      statement.executeUpdate();
      System.out.println("Message created successfully.");
    } catch (SQLException e) {
      System.out.println("Error creating message: " + e.getMessage());
    }
  }

  @Override
  public Message findById(int id) {
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
      System.out.println("Error finding message: " + e.getMessage());
    }
    return null;
  }

  public List<Message> findAllByChat(int chatId) {
    List<Message> messages = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_CHAT_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, chatId);
      ResultSet resultSet = statement.executeQuery();

      while (resultSet.next()) {
        messages.add(mapResultSetToEntity(resultSet));
      }
    } catch (SQLException e) {
      System.out.println("Error finding messages by chat: " + e.getMessage());
    }
    return messages;
  }

  @Override
  public void update(Message message) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_MESSAGE_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setString(1, message.getTextContent());
      statement.setString(2, message.getImageUrl());
      statement.setString(3, message.getVideoUrl());
      statement.setString(4, message.getDocumentUrl());
      statement.setBoolean(5, message.isRead());
      statement.setInt(6, message.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Message updated successfully.");
      } else {
        System.out.println("No message found with the specified ID.");
      }
    } catch (SQLException e) {
      System.out.println("Error updating message: " + e.getMessage());
    }
  }

  @Override
  protected Message mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    int chatId = resultSet.getInt("chat_id");
    int userId = resultSet.getInt("user_id");
    String textContent = resultSet.getString("text_content");
    String imageUrl = resultSet.getString("image_url");
    String videoUrl = resultSet.getString("video_url");
    String documentUrl = resultSet.getString("document_url");
    boolean isRead = resultSet.getBoolean("isRead");
    LocalDateTime sentAt = LocalDateTime.parse(resultSet.getString("sentAt"), DateTimeFormatter.ISO_DATE_TIME);

    // Fetch user and chat details
    User user = new UsersTable().findById(userId);
    PrivateChat privateChat = new PrivateChatsTable().findById(chatId);
    if (privateChat != null) {
      // Load users in the chat
      List<User> usersInChat = new PrivateChatsUsersTable().findUsersByChatId(chatId);
      if (usersInChat.size() == 2) {
        privateChat.setUser1(usersInChat.get(0));
        privateChat.setUser2(usersInChat.get(1));
      }
    }

    return new Message(id, privateChat, user, null, textContent, imageUrl, videoUrl, documentUrl, isRead, sentAt);
  }

}
