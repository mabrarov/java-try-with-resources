package org.mabrarov.javatrywithresources.emulation;

public class TryWithResourceTemplateNative<T extends AutoCloseable> implements
    TryWithResourceTemplate<T> {

  @Override
  public <V> V execute(ResourceProducer<T> producer, ResourceConsumer<? super T, V> consumer)
      throws Exception {
    try (T resource = producer.produce()) {
      return consumer.consume(resource);
    }
  }

}
