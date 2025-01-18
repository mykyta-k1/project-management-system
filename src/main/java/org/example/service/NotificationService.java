package org.example.service;

import org.example.database.NotificationsTable;
import org.example.models.Message;
import org.example.models.NotificationImpl;
import org.example.models.User;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationService {

  private final NotificationsTable notificationsTable;

  public NotificationService() {
    this.notificationsTable = new NotificationsTable();
  }

  // Оповіщення про одне повідомлення
  public void notifySingleMessage(User user, Message message) {
    if (!message.isRead()) {
      // Локальна логіка
      message.getPrivateChat().getHistoryChat().addNotification(user, message);
      sendNotification(user, "У вас нове повідомлення від " + message.getUser().getUsername() + ": " + message.getTextContent());

      // Запис у БД
      NotificationImpl notification = new NotificationImpl(
          0, // ID встановлюється БД
          user.getId(),
          message.getPrivateChat().getId(),
          message.getId(),
          false,
          LocalDateTime.now()
      );
      notificationsTable.create(notification);
    }
  }

  // Відправка повідомлення у консоль
  private void sendNotification(User user, String notificationText) {
    System.out.println("Сповіщення для " + user.getUsername() + ": " + notificationText);
  }

  // Оповіщення про непрочитані повідомлення
  public void notifyUnreadMessages(User user, List<Message> messages) {
    for (Message message : messages) {
      if (!message.isRead()) {
        sendNotification(user, "У вас нове повідомлення від " + message.getUser().getUsername());

        // Запис у БД для кожного повідомлення
        NotificationImpl notification = new NotificationImpl(
            0, // ID встановлюється БД
            user.getId(),
            message.getPrivateChat().getId(),
            message.getId(),
            false,
            LocalDateTime.now()
        );
        notificationsTable.create(notification);
      }
    }
  }
}
