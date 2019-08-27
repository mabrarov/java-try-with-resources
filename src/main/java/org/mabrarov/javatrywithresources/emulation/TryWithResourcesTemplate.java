package org.mabrarov.javatrywithresources.emulation;

public interface TryWithResourcesTemplate<T extends AutoCloseable> {

  <V> V execute(ResourceConsumer<? super T, V> consumer) throws Exception;

}
