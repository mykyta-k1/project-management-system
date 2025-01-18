package org.example.database;

public class GroupChatUsersTable {

  private static final String CREATE_GROUP_CHAT_USERS_TABLE = """
      CREATE TABLE IF NOT EXISTS group_chat_users (
          chat_id INTEGER NOT NULL,
          user_id INTEGER NOT NULL,
          PRIMARY KEY (chat_id, user_id),
          FOREIGN KEY (chat_id) REFERENCES group_chats(id) ON DELETE CASCADE,
          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
      );
      
      CREATE INDEX IF NOT EXISTS idx_group_chat_id ON group_chat_users(chat_id);
      CREATE INDEX IF NOT EXISTS idx_group_user_id ON group_chat_users(user_id);
      """;

}
