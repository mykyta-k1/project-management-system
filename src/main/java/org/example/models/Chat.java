package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.enums.RoleType;
import org.example.service.NotificationService;

/**
 * Клас чату, який містить список користувачів які присутні в ньому,
 * та використовується в ролі посередника між спілкуванням користувачів.
 * @version 1.1
 */
public class Chat {
  private int id;
  private String name;
  private List<User> usersInChat;
  private Map<User, RoleType> roles;
  private String chatImageUrl;
  private HistoryChat historyChat;
  private LocalDateTime createdAt;
  private NotificationService notificationService;
  private Map<User, Integer> unreadMessagesCount;

  public Chat(int id, String name, String chatImageUrl) {
    this.id = id;
    this.name = name;
    this.usersInChat = new ArrayList<>();
    this.chatImageUrl = chatImageUrl;
    this.roles = new HashMap<>();
    this.createdAt = LocalDateTime.now();
    this.historyChat = new HistoryChat();
    this.notificationService = new NotificationService();
    this.unreadMessagesCount = new HashMap<>();
  }

  // Маркування повідомлень як прочитаних
  public void markMessagesAsRead(User user) {
    for (Message message : historyChat.getMessages()) {
      if (!message.isRead() && !message.getUser().equals(user)) {
        message.setRead(true);
        decrementUnreadMessagesCount(user); // Зменшуємо лічильник на 1
        System.out.println("Користувач " + user.getUsername() + " прочитав повідомлення від " + message.getUser().getUsername());
      }
    }
  }

  private void decrementUnreadMessagesCount(User user) {
    int currentCount = unreadMessagesCount.getOrDefault(user, 0);
    if (currentCount > 0) {
      unreadMessagesCount.put(user, currentCount - 1);
    }
  }

  // Метод для оновлення лічильника непрочитаних повідомлень
  public void updateUnreadMessagesCount(User sender) {
    for (User user : usersInChat) {
      if (!user.equals(sender)) {
        unreadMessagesCount.put(user, unreadMessagesCount.getOrDefault(user, 0) + 1);
      }
    }
  }

  // Метод для скидання лічильника непрочитаних повідомлень
  public void resetUnreadMessagesCount(User user) {
    unreadMessagesCount.put(user, 0);
  }

  // Метод для отримання кількості непрочитаних повідомлень для конкретного користувача
  public int getUnreadMessagesCount(User user) {
    return unreadMessagesCount.getOrDefault(user, 0);
  }

  public void addUser(User user) {
    if (!usersInChat.contains(user)) {
      usersInChat.add(user);
      System.out.println(user.getUsername() + " приєднався до чату " + name);
    } else {
      System.out.println(user.getUsername() + " вже у чаті " + name);
    }
  }

  public void removeUser(User user) {
    if (usersInChat.remove(user)) {
      System.out.println(user.getUsername() + " покинув чат " + name);
    } else {
      System.out.println(user.getUsername() + " не знайдено в чаті.");
    }
  }

  public void sendMessageUsers(Message message) {
    if (usersInChat.isEmpty() || !usersInChat.contains(message.getUser())) {
      System.out.println("Користувач " + message.getUser().getUsername() +
          " не може надіслати повідомлення, оскільки не доданий до чату.");
      return;
    }

    historyChat.addMessage(message);
    System.out.println("[" + name + "] " + message.getUser().getUsername() + " надіслав повідомлення: " + message.getTextContent());
    updateUnreadMessagesCount(message.getUser());
    for (User user : usersInChat) {
      if (!user.equals(message.getUser())) {
        unreadMessagesCount.put(user, unreadMessagesCount.getOrDefault(user, 0) + 1);
        notificationService.notifySingleMessage(user, message);
      }
    }
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public HistoryChat getHistoryChat() {
    return historyChat;
  }
}
