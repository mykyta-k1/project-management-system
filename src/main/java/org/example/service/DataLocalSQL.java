package org.example.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class DataLocalSQL<T> {

  protected final String url = "jdbc:sqlite:src/main/resources/messenger_local_db.db";

  protected Connection getConnection() throws Exception {
    return DriverManager.getConnection(url);
  }

  // CRUD
  public abstract void create(T entity);

  public abstract T findById(int id);

  public abstract List<T> findAll();

  public abstract void update(T entity);

  public abstract void deleteById(int id);

  // Search in the table by id
  public boolean exists(int id) {
    String sql = "SELECT 1 FROM " + getTableName() + " WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();
      return rs.next();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  protected abstract String getTableName();
}
