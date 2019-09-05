package org.mabrarov.javatrywithresources;

public class ExceptionGenerator {

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

  void tryFinally() throws Exception {
    Resource resource = new Resource();
    try {
      consumeResource(resource);
    } finally {
      resource.close();
    }
  }

  void tryWithResources() throws Exception {
    try (Resource resource = new Resource()) {
      consumeResource(resource);
    }
  }

  private void consumeResource(Resource resource) throws ResourceConsumeException {
    throw new ResourceConsumeException();
  }

}
