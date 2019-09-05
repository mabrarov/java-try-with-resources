package org.mabrarov.javatrywithresources;

import java.io.IOException;
import org.junit.Test;

public class TryFinallyUseResourceAfterCloseTest {

  @Test(expected = IOException.class)
  public void test_readFromExistingFile_IOExceptionThrown() throws IOException {
    new TryFinallyUseResourceAfterClose().readFirstLineFromFile(IntroductionTest.FILE);
  }

}
