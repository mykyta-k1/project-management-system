package org.example.models;

import java.time.LocalDateTime;

public class NotificationImpl implements Notification {
  private final int id;
  private final int userId;
  private final int chatId;
  private final int messageId;
  private final boolean isRead;
  private final LocalDateTime createdAt;

  public NotificationImpl(int id, int userId, int chatId, int messageId, boolean isRead, LocalDateTime createdAt) {
    this.id = id;
    this.userId = userId;
    this.chatId = chatId;
    this.messageId = messageId;
    this.isRead = isRead;
    this.createdAt = createdAt;
  }

  @Override
  public void sendNotification(String user, String message) {
    System.out.println("Сповіщення для " + user + ": " + message);
  }

  // Геттери для роботи з БД
  public int getId() {
    return id;
  }

  public int getUserId() {
    return userId;
  }

  public int getChatId() {
    return chatId;
  }

  public int getMessageId() {
    return messageId;
  }

  public boolean isRead() {
    return isRead;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
