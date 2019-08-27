package org.mabrarov.javatrywithresources.emulation;

public interface TryWithResourceTemplate<T extends AutoCloseable> {

  <V> V execute(ResourceProducer<T> resourceProducer, ResourceConsumer<? super T, V> consumer)
      throws Exception;

}
