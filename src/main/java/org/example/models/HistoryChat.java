package org.example.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Клас для збереження/підвантаження історії повідомлень з чату.
 * @version 1.1
 * */
public class HistoryChat {
  private static final int MAX_NOTIFICATIONS = 20;
  private List<Message> messages;
  private Map<User, List<Message>> notificationsHistory;

  public HistoryChat() {
    this.messages = new ArrayList<>();
    this.notificationsHistory = new HashMap<>();
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public void addNotification(User user, Message message) {
    notificationsHistory.putIfAbsent(user, new ArrayList<>());

    List<Message> userNotifications = notificationsHistory.get(user);
    if (userNotifications.size() >= MAX_NOTIFICATIONS) {
      userNotifications.remove(0);
    }
    if (!userNotifications.contains(message)) {
      userNotifications.add(message);
    }
  }

  public List<Message> getNotificationsForUser(User user) {
    return notificationsHistory.getOrDefault(user, new ArrayList<>());
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void clearHistory() {
    messages.clear();
    System.out.println("Історія чату очищена.");
  }

  public List<Message> findMessagesByUser(User user) {
    return messages.stream()
        .filter(message -> message.getUser().equals(user))
        .collect(Collectors.toList());
  }

  public List<Message> findMessagesByContent(String content) {
    return messages.stream()
        .filter(message -> message.getTextContent().contains(content))
        .collect(Collectors.toList());
  }

  public boolean removeMessage(int messageId) {
    return messages.removeIf(message -> message.getId() == messageId);
  }
}
