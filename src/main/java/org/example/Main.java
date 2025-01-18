package org.example;

import java.util.List;
import org.example.enums.MessageType;
import org.example.models.Chat;
import org.example.models.Message;
import org.example.models.PrivateChat;
import org.example.models.User;

public class Main {
  public static void main(String[] args) {
    User user1 = new User(1, "Alex", "alex@example.com", "password123", null);
    User user2 = new User(2, "Maria", "maria@example.com", "password456", null);
    PrivateChat privateChat = new PrivateChat(1, user1, user2);

    Message message1 = new Message(1, privateChat, user1, List.of(MessageType.TEXT), "Привіт, як справи?", null, null, null);
    privateChat.sendMessage(message1);

    privateChat.getHistoryChat().getNotificationsForUser(user2).forEach(notification -> {
      System.out.println("Сповіщення для Maria: " + notification.getTextContent());
    });

  }
}
