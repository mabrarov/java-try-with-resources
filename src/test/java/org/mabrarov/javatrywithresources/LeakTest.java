package org.mabrarov.javatrywithresources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

public class LeakTest {

  private final AtomicBoolean closed = new AtomicBoolean();

  private class FileReader extends java.io.FileReader {

    public FileReader(final String fileName) throws FileNotFoundException {
      super(fileName);
    }

    @Override
    public void close() throws IOException {
      closed.set(true);
      super.close();
    }
  }

  @Test
  public void test_wrongTryWithResources_bufferedReaderException_fileReaderIsNotClosed()
      throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(IntroductionTest.TEXT_FILE), 0)) {
      Assert.fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException ignored) {
      // Expected
    }
    Assert.assertFalse(closed.get());
  }

  @Test
  public void test_chainedTryWithResources_bufferedReaderException_fileReaderIsClosed()
      throws IOException {
    try (FileReader fileReader = new FileReader(IntroductionTest.TEXT_FILE);
         BufferedReader reader = new BufferedReader(fileReader, 0)) {
      Assert.fail("Should throw IllegalArgumentException");
    } catch (IllegalArgumentException ignored) {
      // Expected
    }
    Assert.assertTrue(closed.get());
  }

}
