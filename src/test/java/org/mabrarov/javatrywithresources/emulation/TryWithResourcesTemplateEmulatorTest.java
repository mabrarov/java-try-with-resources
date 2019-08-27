package org.mabrarov.javatrywithresources.emulation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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

  private static final Object[][] PARAMETERS = {
      {
        new TemplateNativeFactory<TestResource>(),
        TryWithResourcesTemplateNative.class.getSimpleName()
      },
      {
        new TemplateEmulatorFactory<TestResource>(),
        TryWithResourcesTemplateEmulator.class.getSimpleName()
      }
  };

  @Parameters(name = "{1}")
  public static Object[][] data() {
    return PARAMETERS;
  }

  @Parameter
  public TemplateFactory<TestResource> templateFactory;

  @Parameter(1)
  public String templateClassName;

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

    verify(resourceProducer).produce();
    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(resourceConsumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(sameInstance(resource)));
    verify(resource).close();
    assertThat(templateResult, is(sameInstance(consumerResult)));
  }

  @Test
  public void test_nullResource_noExceptionWhenResourceClosed() throws Exception {
    final TestConsumerResult consumerResult = new TestConsumerResult();
    ResourceProducer<TestResource> resourceProducer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return null;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> resourceConsumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return consumerResult;
          }
        });

    TestConsumerResult templateResult = templateFactory.create(resourceProducer)
        .execute(resourceConsumer);

    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(resourceConsumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(nullValue()));
    assertThat(templateResult, is(sameInstance(consumerResult)));
  }

  @Test
  public void test_producerThrowsException_consumerIsNotCalled() throws Exception {
    test_producerThrows_consumerNotCalled(new TestException());
  }

  @Test
  public void test_producerThrowsRuntimeException_consumerIsNotCalled() throws Exception {
    test_producerThrows_consumerNotCalled(new TestRuntimeException());
  }

  @Test
  public void test_producerThrowsError_consumerIsNotCalled() throws Exception {
    test_producerThrows_consumerNotCalled(new TestError());
  }

  @Test
  public void test_consumerThrowsException_resourceClosed() throws Exception {
    test_consumerThrows_resourceClosed(new TestException());
  }

  @Test
  public void test_consumerThrowsRuntimeException_resourceClosed() throws Exception {
    test_consumerThrows_resourceClosed(new TestRuntimeException());
  }

  @Test
  public void test_consumerThrowsError_resourceClosed() throws Exception {
    test_consumerThrows_resourceClosed(new TestError());
  }

  private void test_producerThrows_consumerNotCalled(final Throwable producerException)
      throws Exception {
    ResourceProducer<TestResource> resourceProducer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() throws Exception {
        if (producerException instanceof Exception) {
          throw (Exception) producerException;
        }
        throw (Error) producerException;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> resourceConsumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return null;
          }
        });

    try {
      templateFactory.create(resourceProducer).execute(resourceConsumer);
      fail("Expected " + producerException);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(producerException)));
    }

    verify(resourceConsumer, never()).consume(any(TestResource.class));
  }

  private void test_consumerThrows_resourceClosed(final Throwable consumerThrowable)
      throws Exception {
    final TestResource resource = spy(new TestResource());
    ResourceProducer<TestResource> resourceProducer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> resourceConsumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) throws Exception {
            if (consumerThrowable instanceof Exception) {
              throw (Exception) consumerThrowable;
            }
            throw (Error) consumerThrowable;
          }
        });

    try {
      templateFactory.create(resourceProducer).execute(resourceConsumer);
      fail("Expected " + consumerThrowable);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(consumerThrowable)));
    }

    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(resourceConsumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(sameInstance(resource)));
    verify(resource).close();
  }

}
