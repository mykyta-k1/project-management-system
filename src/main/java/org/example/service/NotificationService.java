package org.example.service;

import java.util.List;
import org.example.models.Message;
import org.example.models.User;

public class NotificationService {

  public void notifySingleMessage(User user, Message message) {
    if (!message.isRead()) {
      message.getPrivateChat().getHistoryChat().addNotification(user, message);
      sendNotification(user, "У вас нове повідомлення від " + message.getUser().getUsername() + ": " + message.getTextContent());
    }
  }

  private void sendNotification(User user, String notificationText) {
    System.out.println("Сповіщення для " + user.getUsername() + ": " + notificationText);
  }

  public void notifyUnreadMessages(User user, List<Message> messages) {
    for (Message message : messages) {
      if (!message.isRead()) {
        sendNotification(user, "У вас нове повідомлення від " + message.getUser().getUsername());
      }
    }
  }
}
