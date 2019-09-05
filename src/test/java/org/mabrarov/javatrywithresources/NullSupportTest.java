package org.mabrarov.javatrywithresources;

import org.junit.Test;

public class NullSupportTest {

  @Test
  public void test_nullResource_noException() throws Exception {
    new NullSupport() {
      @Override
      AutoCloseable createResource() {
        return null;
      }
    }.run();
  }

  @Test
  public void test_notNullResource_noException() throws Exception {
    new NullSupport() {
      @Override
      AutoCloseable createResource() {
        return new AutoCloseable() {
          @Override
          public void close() {
          }

          @Override
          public String toString() {
            return "not null";
          }
        };
      }
    }.run();
  }

  @Test
  public void test_randomResource_noException() throws Exception {
    for (int i = 0; i < 10; ++i) {
      new NullSupport().run();
    }
  }

}
