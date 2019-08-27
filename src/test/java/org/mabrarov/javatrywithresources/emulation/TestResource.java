package org.mabrarov.javatrywithresources.emulation;

public class TestResource implements AutoCloseable {

  private final int id;

  public TestResource() {
    this(0);
  }

  public TestResource(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public void close() throws Exception {
    // Nothing to do here
  }

  @Override
  public String toString() {
    return "TestResource{" + "id=" + id + '}';
  }

}
