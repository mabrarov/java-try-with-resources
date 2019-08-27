package org.mabrarov.javatrywithresources.emulation;

public class TestRuntimeException extends RuntimeException {

  private final int id;

  public TestRuntimeException() {
    this(0);
  }

  public TestRuntimeException(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "TestRuntimeException{" + "id=" + id + '}';
  }

}
