package org.mabrarov.javatrywithresources.emulation;

public class TestException extends Exception {

  private final String origin;

  public TestException(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " thrown by " + origin + '}';
  }

}
