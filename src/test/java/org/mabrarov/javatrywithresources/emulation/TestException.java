package org.mabrarov.javatrywithresources.emulation;

public class TestException extends Exception {

  private final int id;

  public TestException() {
    this(0);
  }

  public TestException(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "TestException{" + "id=" + id + '}';
  }

}
