package org.example.models;

import java.time.LocalDateTime;
import java.util.List;
import org.example.enums.MessageType;

/**
 * Клас повідомлення для чату.
 * @version 2.0
 */
public class Message {
  private int id;
  private PrivateChat privateChat;
  private User user;
  private boolean isRead;
  private List<MessageType> messageTypes;
  private String textContent;
  private String imageUrl;
  private String videoUrl;
  private String documentUrl;
  private LocalDateTime sentAt;

  public Message(int id, PrivateChat privateChat, User user, List<MessageType> messageTypes,
      String textContent, String imageUrl, String videoUrl, String documentUrl) {
    this.id = id;
    this.privateChat = privateChat;
    this.user = user;
    this.messageTypes = messageTypes;
    this.textContent = textContent;
    this.imageUrl = imageUrl;
    this.videoUrl = videoUrl;
    this.documentUrl = documentUrl;
    this.isRead = false;
    this.sentAt = LocalDateTime.now();
  }

  public int getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public boolean isRead() {
    return isRead;
  }

  public void setRead(boolean isRead) {
    this.isRead = isRead;
  }

  public List<MessageType> getMessageTypes() {
    return messageTypes;
  }

  public boolean containsType(MessageType type) {
    return messageTypes.contains(type);
  }

  public String getTextContent() {
    return textContent;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public String getDocumentUrl() {
    return documentUrl;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public PrivateChat getPrivateChat() {
    return privateChat;
  }

  public void setPrivateChat(PrivateChat privateChat) {
    this.privateChat = privateChat;
  }

  @Override
  public String toString() {
    return "Message{" +
        "id=" + id +
        ", user=" + user.getUsername() +
        ", messageTypes=" + messageTypes +
        ", textContent='" + textContent + '\'' +
        ", imageUrl='" + imageUrl + '\'' +
        ", videoUrl='" + videoUrl + '\'' +
        ", documentUrl='" + documentUrl + '\'' +
        ", sentAt=" + sentAt +
        '}';
  }
}
