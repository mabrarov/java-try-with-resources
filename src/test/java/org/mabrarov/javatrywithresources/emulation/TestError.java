package org.mabrarov.javatrywithresources.emulation;

public class TestError extends Error {

  private final String origin;

  public TestError(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " thrown by " + origin;
  }

}
