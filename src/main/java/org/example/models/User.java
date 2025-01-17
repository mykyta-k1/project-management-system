package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Клас який представляє користувача і його можливості та властивості.
 * @version 1.1
 */
public class User {
  private static final String DEFAULT_AVATAR_URL = "/Images/default_avatar.png";
  private int id;
  private String username;
  private String email;
  private String password;
  private List<Chat> chats;
  private Map<Chat, String> rolesInChats;
  private boolean online;
  private String lastSeen;
  private String avatarUrl;
  private LocalDateTime lastActive;

  public User(int id, String username, String email, String password, String avatarUrl) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.chats = new ArrayList<>();
    this.online = false;
    this.lastSeen = null;
    this.rolesInChats = new HashMap<>();

    setAvatarUrl(avatarUrl);
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<Chat> getChats() {
    return chats;
  }

  public void setChats(List<Chat> chats) {
    this.chats = chats;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public String getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(String lastSeen) {
    this.lastSeen = lastSeen;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    if (avatarUrl == null || avatarUrl.isEmpty()) {
      this.avatarUrl = DEFAULT_AVATAR_URL;
    } else {
      this.avatarUrl = avatarUrl;
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User user = (User) obj;
    return id == user.id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }
  /*
  @Override
  public void sendNotification(String fromUser, String content) {
    System.out.println("Сповіщення для " + username + ": нове повідомлення від " + fromUser + ": " + content);
  }
  */

}
