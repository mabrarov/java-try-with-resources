package org.mabrarov.javatrywithresources.emulation;

public interface ResourceProducer<T extends AutoCloseable> {

  T produce() throws Exception;

}
