package org.mabrarov.javatrywithresources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Introduction {

  public static void main(String[] args) throws IOException {
    new Introduction().run(args);
  }

  private void run(String[] args) throws IOException {
    System.out.println(readFirstLineFromFile(args[0]));
  }

  private String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      return reader.readLine();
    } // reader.close() is called at this line
  }

}
