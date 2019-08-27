package org.mabrarov.javatrywithresources.emulation;

public class TryWithResourcesTemplateNative<T extends AutoCloseable> implements
    TryWithResourcesTemplate<T> {

  private final ResourceProducer<T> producer;

  public TryWithResourcesTemplateNative(final ResourceProducer<T> producer) {
    this.producer = producer;
  }

  @Override
  public <V> V execute(ResourceConsumer<? super T, V> consumer) throws Exception {
    try (T resource = producer.produce()) {
      return consumer.consume(resource);
    }
  }

}
