package org.example.service;

import java.io.File;
import java.io.IOException;

public class LocalDataBaseService {
  public static final String URL_LOCAL_DATA_SQL = "jdbc:sqlite:src/main/resources/my_database.db";

  public static boolean isExsistFileSQL() {
    File file = new File(URL_LOCAL_DATA_SQL);
    return file.isFile();
  }

  public static boolean isValidFileSQL() {
    File file = new File("src/main/resources/my_database.db");
    try {
      if (!file.exists()) {
        return file.createNewFile();
      }
      return true;
    } catch (IOException e) {
      throw new RuntimeException("Помилка перевірки або створення файлу БД", e);
    }
  }



}
