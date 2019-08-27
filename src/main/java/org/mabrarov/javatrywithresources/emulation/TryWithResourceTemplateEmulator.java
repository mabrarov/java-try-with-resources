package org.mabrarov.javatrywithresources.emulation;

public class TryWithResourceTemplateEmulator<T extends AutoCloseable> implements
    TryWithResourceTemplate<T> {

  @Override
  public <V> V execute(ResourceProducer<T> producer, ResourceConsumer<? super T, V> consumer)
      throws Exception {
    final T resource = producer.produce();
    try {
      return consumer.consume(resource);
    } finally {
      resource.close();
    }
  }

}
