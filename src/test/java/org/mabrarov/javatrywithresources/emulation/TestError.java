package org.mabrarov.javatrywithresources.emulation;

public class TestError extends Error {

  private final int id;

  public TestError() {
    this(0);
  }

  public TestError(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "TestError{" + "id=" + id + '}';
  }

}
