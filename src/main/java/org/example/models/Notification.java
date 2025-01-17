package org.example.models;

/**
 * Інтерфейс для сповіщення користувачів що у чаті.
 * @version 1.0
 * */
public interface Notification {
  void sendNotification(String user, String message);
}
