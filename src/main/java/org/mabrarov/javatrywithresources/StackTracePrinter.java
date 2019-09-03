package org.mabrarov.javatrywithresources;

public class StackTracePrinter {

  private static class ResourceConsumeException extends Exception {

    private ResourceConsumeException() {
      super("Failed to consume resource");
    }
  }

  private static class ResourceCloseException extends Exception {

    private ResourceCloseException() {
      super("Failed to close resource");
    }
  }

  private static class Resource implements AutoCloseable {

    @Override
    public void close() throws ResourceCloseException {
      throw new ResourceCloseException();
    }

  }

  public static void main(String[] args) {
    new StackTracePrinter().run();
  }

  private void run() {
    try {
      tryFinally();
    } catch (Exception e) {
      printStackTrace("\ntry-finally stacktrace:\n", e);
    }

    try {
      tryWithResources();
    } catch (Exception e) {
      printStackTrace("\ntry-with-resources stacktrace:\n", e);
    }
  }

  private void printStackTrace(String header, Throwable throwable) {
    System.err.println(header);
    throwable.printStackTrace();
  }

  private void tryFinally() throws Exception {
    Resource resource = new Resource();
    try {
      consumeResource(resource);
    } finally {
      resource.close();
    }
  }

  private void tryWithResources() throws Exception {
    try (Resource resource = new Resource()) {
      consumeResource(resource);
    }
  }

  private void consumeResource(Resource resource) throws ResourceConsumeException {
    throw new ResourceConsumeException();
  }

}
