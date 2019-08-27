package org.mabrarov.javatrywithresources.emulation;

public class TestRuntimeException extends RuntimeException {

  private final String origin;

  public TestRuntimeException(String origin) {
    this.origin = origin;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " thrown by " + origin + '}';
  }

}
