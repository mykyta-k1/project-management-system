package org.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class DataLocalSQL<T> {

  // Метод для створення таблиці (використовується SQL-скрипт)
  public void createTable(String createTableSQL) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
      statement.execute();
      System.out.println("Table created successfully.");
    } catch (SQLException e) {
      System.out.println("Error creating table: " + e.getMessage());
    }
  }

  // Абстрактний метод для створення запису
  public abstract void create(T entity);

  // Абстрактний метод для знаходження запису за ID
  public abstract T findById(int id);

  // Метод для знаходження всіх записів
  public List<T> findAll(String querySQL) {
    List<T> resultList = new ArrayList<>();
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(querySQL);
        ResultSet resultSet = statement.executeQuery()) {

      while (resultSet.next()) {
        T entity = mapResultSetToEntity(resultSet);
        resultList.add(entity);
      }
    } catch (SQLException e) {
      System.out.println("Error finding records: " + e.getMessage());
    }
    return resultList;
  }

  // Абстрактний метод для оновлення запису
  public abstract void update(T entity);

  // Метод для видалення запису за ID
  public void deleteById(String deleteSQL, int id) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
      statement.setInt(1, id);
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Record deleted successfully.");
      } else {
        System.out.println("No record found with the specified ID.");
      }
    } catch (SQLException e) {
      System.out.println("Error deleting record: " + e.getMessage());
    }
  }

  // Метод для мапінгу ResultSet у конкретний об'єкт (реалізація у дочірніх класах)
  protected abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;
}
