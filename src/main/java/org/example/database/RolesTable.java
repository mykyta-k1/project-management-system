package org.example.database;

import org.example.models.Role;
import org.example.service.DataLocalSQL;
import org.example.service.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolesTable extends DataLocalSQL<Role> {

  private static final String CREATE_ROLES_TABLE = """
    CREATE TABLE IF NOT EXISTS roles (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        role_name TEXT NOT NULL UNIQUE,
        permissions TEXT NOT NULL
    );
""";

  private static final String INSERT_ROLE_SQL = """
        INSERT INTO roles (role_name, permissions)
        VALUES (?, ?);""";

  private static final String FIND_BY_ID_SQL = "SELECT * FROM roles WHERE id = ?";
  private static final String FIND_BY_NAME_SQL = "SELECT * FROM roles WHERE role_name = ?";
  private static final String UPDATE_ROLE_SQL = """
        UPDATE roles
        SET role_name = ?, permissions = ?
        WHERE id = ?;""";
  private static final String DELETE_ROLE_SQL = "DELETE FROM roles WHERE id = ?";

  public RolesTable() {
    createTable(CREATE_ROLES_TABLE);
  }

  @Override
  public void create(Role role) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_ROLE_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setString(1, role.getRoleName());
      statement.setString(2, String.join(",", role.getPermissions()));
      statement.executeUpdate();

      System.out.println("Role created successfully.");
    } catch (SQLException e) {
      System.out.println("Error creating role: " + e.getMessage());
    }
  }

  @Override
  public Role findById(int id) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return mapResultSetToEntity(resultSet);
      }
    } catch (SQLException e) {
      System.out.println("Error finding role: " + e.getMessage());
    }
    return null;
  }

  public Role findByName(String roleName) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setString(1, roleName);
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        return mapResultSetToEntity(resultSet);
      }
    } catch (SQLException e) {
      System.out.println("Error finding role by name: " + e.getMessage());
    }
    return null;
  }

  @Override
  public void update(Role role) {
    try (Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_ROLE_SQL)) {
      if (connection == null) {
        throw new IllegalStateException("Database connection is not available.");
      }
      statement.setString(1, role.getRoleName());
      statement.setString(2, String.join(",", role.getPermissions()));
      statement.setInt(3, role.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Role updated successfully.");
      } else {
        System.out.println("No role found with the specified ID.");
      }
    } catch (SQLException e) {
      System.out.println("Error updating role: " + e.getMessage());
    }
  }

  @Override
  protected Role mapResultSetToEntity(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String roleName = resultSet.getString("role_name");
    String[] permissionsArray = resultSet.getString("permissions").split(",");
    List<String> permissions = List.of(permissionsArray);

    return new Role(id, roleName, permissions);
  }
}
