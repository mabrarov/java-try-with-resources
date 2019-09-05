package org.mabrarov.javatrywithresources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;

public class IntroductionTest {

  static final String TEXT_FILE = "target/test-classes/test.txt";
  private static final String FIRST_LINE_OF_TEXT_FILE = "This is 1st line of test.txt file";

  // @formatter:off
  private static final String DATABASE_STRUCTURE_SQL =
      "CREATE TABLE employee (id INT NOT NULL, name VARCHAR(50) NOT NULL, email VARCHAR(50) NOT NULL, PRIMARY KEY (id))";
  // @formatter:on

  // @formatter:off
  private static final List<String> DATABASE_FILL_SQL = Arrays.asList(
      "INSERT INTO employee VALUES (1001, 'Pacman', 'pacman@example.com')",
      "INSERT INTO employee VALUES (1002, 'Batman', 'batman@example.com')",
      "INSERT INTO employee VALUES (1003, 'Superman', 'batman@example.com')");
  // @formatter:on

  private static final String DATABASE_SELECT_SQL = "SELECT * FROM employee ORDER BY name";
  private static final List<String> NAMES_SORTED = Arrays.asList("Batman", "Pacman", "Superman");
  private static final String NAME_COLUMN = "name";

  @Test
  public void test_readFromExistingFile_existingFile_firstLineIsRead() throws IOException {
    String line = new Introduction().readFirstLineFromFile(TEXT_FILE);
    Assert.assertEquals(FIRST_LINE_OF_TEXT_FILE, line);
  }

  @Test
  public void test_readAllRows_validRequest_allRowsAreRead() throws SQLException {
    List<String> names;
    try (HikariDataSource dataSource = createDatabase()) {
      initDatabase(dataSource);
      names = new Introduction().readAllRows(dataSource, DATABASE_SELECT_SQL, NAME_COLUMN);
    }
    Assert.assertEquals(names, NAMES_SORTED);
  }

  @Test
  public void test_readAllRowsOrEmpty_validRequest_allRowsAreRead() throws SQLException {
    List<String> names;
    try (HikariDataSource dataSource = createDatabase()) {
      initDatabase(dataSource);
      names = new Introduction().readAllRowsOrEmpty(dataSource, DATABASE_SELECT_SQL, NAME_COLUMN);
    }
    Assert.assertEquals(names, NAMES_SORTED);
  }

  @Test
  public void test_readAllRowsOrEmpty_invalidRequest_noneRead() throws SQLException {
    List<String> names;
    try (HikariDataSource dataSource = createDatabase()) {
      initDatabase(dataSource);
      names = new Introduction().readAllRowsOrEmpty(dataSource, "some broken SQL", NAME_COLUMN);
    }
    Assert.assertTrue(names.isEmpty());
  }

  private HikariDataSource createDatabase() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:h2:mem:");
    return new HikariDataSource(config);
  }

  private void initDatabase(DataSource dataSource) throws SQLException {
    // @formatter:off
    try (Connection connection = dataSource.getConnection();
         Statement statement = connection.createStatement()) {
    // @formatter:on
      statement.execute(DATABASE_STRUCTURE_SQL);
      connection.commit();
      for (String sql : DATABASE_FILL_SQL) {
        statement.executeUpdate(sql);
      }
      connection.commit();
    }
  }


}
