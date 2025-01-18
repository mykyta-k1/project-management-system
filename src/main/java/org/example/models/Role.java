package org.example.models;

import java.util.List;

public class Role {
  private int id;
  private String roleName;
  private List<String> permissions; // send_message, delete_message, etc.

  public Role(int id, String roleName, List<String> permissions) {
    this.id = id;
    this.roleName = roleName;
    this.permissions = permissions;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }
}

