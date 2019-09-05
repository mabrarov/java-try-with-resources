package org.mabrarov.javatrywithresources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

public class Introduction {

  String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      return reader.readLine();
    } // reader.close() is called at this line
  }

  List<String> readAllRows(DataSource dataSource, String sql, String column) throws SQLException {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {
      List<String> rows = new ArrayList<>();
      while (resultSet.next()) {
        rows.add(resultSet.getString(column));
      }
      return rows;
    } // resultSet.close(), statement.close(), connection.close()
  }

  List<String> readAllRowsOrEmpty(DataSource dataSource, String sql, String column) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      List<String> rows = new ArrayList<>();
      while (resultSet.next()) {
        rows.add(resultSet.getString(column));
      }
      return rows;
    } catch (SQLException ignored) {
      return Collections.emptyList();
    }
  }

}
