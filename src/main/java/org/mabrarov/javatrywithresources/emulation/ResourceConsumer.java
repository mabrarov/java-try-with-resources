package org.mabrarov.javatrywithresources.emulation;

public interface ResourceConsumer<T extends AutoCloseable, V> {

  V consume(T resource) throws Exception;

}
