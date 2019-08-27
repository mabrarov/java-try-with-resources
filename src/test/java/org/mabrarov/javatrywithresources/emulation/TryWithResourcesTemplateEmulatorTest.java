package org.mabrarov.javatrywithresources.emulation;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;

@RunWith(Parameterized.class)
public class TryWithResourcesTemplateEmulatorTest {

  public interface TemplateFactory<T extends AutoCloseable> {

    TryWithResourcesTemplate<T> create(ResourceProducer<T> producer);

  }

  public static class TemplateNativeFactory<T extends AutoCloseable> implements TemplateFactory<T> {

    @Override
    public TryWithResourcesTemplate<T> create(ResourceProducer<T> producer) {
      return new TryWithResourcesTemplateNative<>(producer);
    }

  }

  public static class TemplateEmulatorFactory<T extends AutoCloseable> implements
      TemplateFactory<T> {

    @Override
    public TryWithResourcesTemplate<T> create(ResourceProducer<T> producer) {
      return new TryWithResourcesTemplateEmulator<>(producer);
    }

  }

  @Parameters
  public static List<TemplateFactory<TestResource>> data() {
    return Arrays.asList(new TemplateNativeFactory<TestResource>(),
        new TemplateEmulatorFactory<TestResource>());
  }

  @Parameter
  public TemplateFactory<TestResource> templateFactory;

  @Test
  public void test_produceResource_resourceIsPassedToConsumer() throws Exception {
    final TestResource resource = spy(new TestResource());
    final TestConsumerResult consumerResult = new TestConsumerResult();
    ResourceProducer<TestResource> resourceProducer = spy(new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    });
    ResourceConsumer<TestResource, TestConsumerResult> resourceConsumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return consumerResult;
          }
        });

    TestConsumerResult templateResult = templateFactory.create(resourceProducer)
        .execute(resourceConsumer);

    verify(resourceProducer, times(1)).produce();
    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(resourceConsumer, times(1)).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), sameInstance(resource));
    verify(resource, times(1)).close();
    assertThat(templateResult, sameInstance(consumerResult));
  }
}
