package org.example.database;

import org.example.service.DataLocalSQL;

public class GroupChatsTable {

  private static final String CREATE_GROUP_CHATS_TABLE = """
      CREATE TABLE IF NOT EXISTS group_chats (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          name TEXT NOT NULL,
          createdAt TEXT NOT NULL
      );
      """;


}
