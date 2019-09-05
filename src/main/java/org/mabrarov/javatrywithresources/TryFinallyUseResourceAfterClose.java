package org.mabrarov.javatrywithresources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryFinallyUseResourceAfterClose {

  String readFirstLineFromFile(String path) throws IOException {
    String line;
    BufferedReader reader = new BufferedReader(new FileReader(path));
    try {
      line = reader.readLine();
    } finally {
      reader.close();
    }
    reader.readLine(); // throws IOException: Stream closed
    return line;
  }
}
