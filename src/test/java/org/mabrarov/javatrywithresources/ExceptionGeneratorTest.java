package org.mabrarov.javatrywithresources;

import org.junit.Test;

public class ExceptionGeneratorTest {

  @Test
  public void test_printStackTrace() {
    ExceptionGenerator generator = new ExceptionGenerator();
    try {
      generator.tryFinally();
    } catch (Exception e) {
      printStackTrace("try-finally stacktrace:", e);
    }

    try {
      generator.tryWithResources();
    } catch (Exception e) {
      printStackTrace("try-with-resources stacktrace:", e);
    }
  }

  private void printStackTrace(String header, Throwable throwable) {
    System.out.println(header);
    throwable.printStackTrace(System.out);
  }
}
