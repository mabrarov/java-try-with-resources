package org.mabrarov.javatrywithresources.emulation;

public class TryWithResourcesTemplateEmulator<T extends AutoCloseable> implements
    TryWithResourcesTemplate<T> {

  private final ResourceProducer<T> producer;

  public TryWithResourcesTemplateEmulator(final ResourceProducer<T> producer) {
    this.producer = producer;
  }

  @Override
  public <V> V execute(ResourceConsumer<? super T, V> consumer) throws Exception {
    final T resource = producer.produce();
    try {
      return consumer.consume(resource);
    } finally {
      if (resource != null) {
        resource.close();
      }
    }
  }

}
