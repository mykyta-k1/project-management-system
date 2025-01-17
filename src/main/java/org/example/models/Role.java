package org.example.models;

import java.util.List;

public class Role {
  private String roleName;
  private List<String> permissions; // send_message, delete_message, etc.

  public Role(String roleName, List<String> permissions) {
    this.roleName = roleName;
    this.permissions = permissions;
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

