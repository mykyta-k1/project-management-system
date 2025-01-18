package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.enums.RoleType;
import org.example.service.NotificationService;

public class PrivateChat {
  private int id;
  private User user1;
  private User user2;
  private String chatImageUrl;
  private HistoryChat historyChat;
  private LocalDateTime createdAt;
  private NotificationService notificationService;
  private Map<User, Integer> unreadMessagesCount;
  private Map<User, String> displayNameCache = new HashMap<>();

  public PrivateChat(int id, User user1, User user2) {
    this.id = id;
    this.user1 = user1;
    this.user2 = user2;
    this.historyChat = new HistoryChat();
    this.createdAt = LocalDateTime.now();
    this.notificationService = new NotificationService();
    this.unreadMessagesCount = new HashMap<>();
    this.unreadMessagesCount.put(user1, 0);
    this.unreadMessagesCount.put(user2, 0);
  }

  public void updateUnreadMessagesCount(User sender) {
    User recipient = sender.equals(user1) ? user2 : user1;
    unreadMessagesCount.put(recipient, unreadMessagesCount.getOrDefault(recipient, 0) + 1);
  }


  public void sendMessage(Message message) {
    if (!message.getUser().equals(user1) && !message.getUser().equals(user2)) {
      System.out.println("Користувач " + message.getUser().getUsername() + " не є учасником цього чату.");
      return;
    }

    historyChat.addMessage(message);
    System.out.println("Чат між " + user1.getUsername() + " і " + user2.getUsername() +
        ": " + message.getUser().getUsername() + " надіслав повідомлення: " + message.getTextContent());

    User recipient = message.getUser().equals(user1) ? user2 : user1;
    unreadMessagesCount.put(recipient, unreadMessagesCount.getOrDefault(recipient, 0) + 1);
    notifyUser(recipient, message);
    updateUnreadMessagesCount(message.getUser());
  }

  public void markMessagesAsRead(User user) {
    if (getUnreadMessagesCount(user) == 0) {
      return;
    }
    for (Message message : historyChat.getMessages()) {
      if (!message.isRead() && !message.getUser().equals(user)) {
        message.setRead(true);
        decrementUnreadMessagesCount(user);
        System.out.println("Користувач " + user.getUsername() + " прочитав повідомлення від " + message.getUser().getUsername());
      }
    }
  }

  private void notifyUser(User recipient, Message message) {
    notificationService.notifySingleMessage(recipient, message);
  }

  private void decrementUnreadMessagesCount(User user) {
    int currentCount = unreadMessagesCount.getOrDefault(user, 0);
    if (currentCount > 0) {
      unreadMessagesCount.put(user, currentCount - 1);
    }
  }

  public void showUserNotifications(User user) {
    List<Message> notifications = historyChat.getNotificationsForUser(user);
    System.out.println("Сповіщення для " + user.getUsername() + ":");
    for (Message notification : notifications) {
      System.out.println(notification.getTextContent());
    }
  }

  /**
   * Метод для отримання назви чату, який буде залежати від користувача.
   * @param user Користувач, для якого потрібно отримати назву.
   * @return Назва чату як ім'я іншого користувача.
   */
  public String getDisplayName(User user) {
    if (displayNameCache.containsKey(user)) {
      return displayNameCache.get(user);
    }

    String displayName;
    if (user.equals(user1)) {
      displayName = user2.getUsername();
    } else if (user.equals(user2)) {
      displayName = user1.getUsername();
    } else {
      throw new IllegalArgumentException("Користувач не є учасником цього чату.");
    }

    displayNameCache.put(user, displayName);
    return displayName;
  }

  public String getAvatarForUser(User requester) {
    if (requester.equals(user1)) {
      return user2.getAvatarUrl();
    } else if (requester.equals(user2)) {
      return user1.getAvatarUrl();
    } else {
      throw new IllegalArgumentException("Користувач не є учасником цього чату.");
    }
  }


  public int getUnreadMessagesCount(User user) {
    return unreadMessagesCount.getOrDefault(user, 0);
  }

  public void resetUnreadMessagesCount(User user) {
    unreadMessagesCount.put(user, 0);
  }

  public int getId() {
    return id;
  }

  public HistoryChat getHistoryChat() {
    return historyChat;
  }
}
