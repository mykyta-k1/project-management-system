package org.example;

import org.example.database.*;
import org.example.enums.MessageType;
import org.example.models.*;
import org.example.service.DatabaseConnection;
import org.example.service.NotificationService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    try {
      // Ініціалізація таблиць
      new UsersTable();
      new MessagesTable();
      new PrivateChatsTable();
      new NotificationsTable();
      new PrivateChatsTable();
      new RolesTable();

      System.out.println("All tables and indexes have been created.");
    } finally {
      // Закриття з'єднання
      DatabaseConnection.getInstance().closeConnection();
    }
  }
}
