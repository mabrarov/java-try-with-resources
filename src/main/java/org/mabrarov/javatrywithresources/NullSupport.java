package org.mabrarov.javatrywithresources;

public class NullSupport {

  // @formatter:off
  AutoCloseable createResource() {
    if (Math.random() < 0.5) return null;
    return new AutoCloseable() {
      public void close() {}
      public String toString() {return "not null"; }
    };
  }
  // @formatter:on

  void run() throws Exception {
    try (AutoCloseable resource = createResource()) {
      System.out.println("resource is " + resource);
    } // resource.close() is not called if resource == null
  }

}
