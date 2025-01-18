package org.example.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.models.PrivateChat;
import org.example.service.DataLocalSQL;
import org.example.service.DatabaseConnection;

public class PrivateChatsTable extends DataLocalSQL<PrivateChat> {

  private static final Map<Integer, PrivateChat> chatCache = new ConcurrentHashMap<>();

  private static final String CREATE_PRIVATE_CHATS_TABLE = """
        CREATE TABLE IF NOT EXISTS private_chats (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            createdAt TEXT NOT NULL
        );
        """;

  private static final String INSERT_SQL = """
        INSERT INTO private_chats (createdAt)
        VALUES (?);
    """;

  private static final String FIND_BY_ID_SQL = """
        SELECT * FROM private_chats WHERE id = ?;
    """;

  private static final String UPDATE_SQL = """
        UPDATE private_chats SET createdAt = ? WHERE id = ?;
    """;

  public PrivateChatsTable() {
    createTable(CREATE_PRIVATE_CHATS_TABLE);
  }

  @Override
  public void create(PrivateChat entity) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setString(1, entity.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));

      int affectedRows = statement.executeUpdate();
      if (affectedRows > 0) {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
          entity.setId(generatedKeys.getInt(1));
        }
        System.out.println("Private chat created successfully with ID: " + entity.getId());
      }
    } catch (SQLException e) {
      System.out.println("Error creating private chat: " + e.getMessage());
    }
  }

  @Override
  public PrivateChat findById(int id) {
    // Перевіряємо, чи чат є в кеші
    if (chatCache.containsKey(id)) {
      return chatCache.get(id);
    }

    // Якщо чату немає в кеші, отримуємо його з бази даних
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM private_chats WHERE id = ?")) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        PrivateChat privateChat = mapResultSetToEntity(resultSet);

        // Додаємо чат до кешу
        chatCache.put(id, privateChat);
        return privateChat;
      }
    } catch (SQLException e) {
      System.out.println("Error finding chat: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void update(PrivateChat chat) {
    // Перевіряємо, чи переданий об'єкт має всі необхідні поля
    if (chat == null || chat.getId() == 0 || chat.getCreatedAt() == null) {
      throw new IllegalArgumentException("Invalid PrivateChat object for update.");
    }

    String updateSQL = "UPDATE private_chats SET createdAt = ? WHERE id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(updateSQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      // Оновлюємо поле createdAt
      statement.setString(1, chat.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
      statement.setInt(2, chat.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Private chat updated successfully.");
        // Оновлюємо кеш
        chatCache.put(chat.getId(), chat);
      } else {
        System.out.println("No private chat found with the specified ID.");
      }

    } catch (SQLException e) {
      System.out.println("Error updating private chat: " + e.getMessage());
    }
  }


  @Override
  public void deleteById(String deleteSQL, int id) {
    // Видалення з БД
    super.deleteById(deleteSQL, id);
    // Видалення з кешу
    chatCache.remove(id);
  }


  @Override
  protected PrivateChat mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("createdAt"), DateTimeFormatter.ISO_DATE_TIME);

    return new PrivateChat(id, createdAt);
  }
}
