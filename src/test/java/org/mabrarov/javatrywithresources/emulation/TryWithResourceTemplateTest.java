package org.mabrarov.javatrywithresources.emulation;

import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
public class TryWithResourceTemplateTest {

  public interface TemplateFactory<T extends AutoCloseable> {

    TryWithResourceTemplate<T> create();

  }

  public static class TemplateNativeFactory<T extends AutoCloseable> implements TemplateFactory<T> {

    @Override
    public TryWithResourceTemplate<T> create() {
      return new TryWithResourceTemplateNative<>();
    }

  }

  public static class TemplateEmulatorFactory<T extends AutoCloseable> implements
      TemplateFactory<T> {

    @Override
    public TryWithResourceTemplate<T> create() {
      return new TryWithResourceTemplateEmulator<>();
    }

  }

  // @formatter:off
  private static final Object[][] PARAMETERS = {
      {
        new TemplateNativeFactory<TestResource>(),
        TryWithResourceTemplateNative.class.getSimpleName()
      }
      ,
      {
        new TemplateEmulatorFactory<TestResource>(),
        TryWithResourceTemplateEmulator.class.getSimpleName()
      }
  };
  // @formatter:on

  private static String ORIGIN_PRODUCER = "ResourceProducer#produce method";
  private static String ORIGIN_CONSUMER = "ResourceConsumer#consume method";
  private static String ORIGIN_RESOURCE = "AutoCloseable#close method";
  private static String ORIGIN_THROWABLE_ADD_SUPPRESSED = "Throwable#addSuppressed method";

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
    ResourceProducer<TestResource> producer = spy(new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    });
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return consumerResult;
          }
        });

    TestConsumerResult templateResult = templateFactory.create().execute(producer, consumer);

    verify(producer).produce();
    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(consumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(sameInstance(resource)));
    verify(resource).close();
    assertThat(templateResult, is(sameInstance(consumerResult)));
  }

  @Test
  public void test_nullResource_noExceptionWhenResourceClosed() throws Exception {
    final TestConsumerResult consumerResult = new TestConsumerResult();
    ResourceProducer<TestResource> producer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return null;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return consumerResult;
          }
        });

    TestConsumerResult templateResult = templateFactory.create().execute(producer, consumer);

    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(consumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(nullValue()));
    assertThat(templateResult, is(sameInstance(consumerResult)));
  }

  @Test
  public void test_resourceThrowsException_resourceExceptionIsThrown() throws Exception {
    test_resourceThrowsException_resourceThrowableIsThrown(new TestException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_resourceThrowsRuntimeException_resourceExceptionIsThrown() throws Exception {
    test_resourceThrowsException_resourceThrowableIsThrown(
        new TestRuntimeException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_resourceThrowsError_resourceErrorThrown() throws Exception {
    test_resourceThrowsException_resourceThrowableIsThrown(new TestError(ORIGIN_RESOURCE));
  }

  @Test
  public void test_producerThrowsException_consumerIsNotCalled() throws Exception {
    test_producerThrows_consumerNotCalled(new TestException(ORIGIN_PRODUCER));
  }

  @Test
  public void test_producerThrowsRuntimeException_consumerIsNotCalled() throws Exception {
    test_producerThrows_consumerNotCalled(new TestRuntimeException(ORIGIN_PRODUCER));
  }

  @Test
  public void test_producerThrowsError_consumerIsNotCalled() throws Exception {
    test_producerThrows_consumerNotCalled(new TestError(ORIGIN_PRODUCER));
  }

  @Test
  public void test_consumerThrowsException_resourceClosed() throws Exception {
    test_consumerThrows_resourceClosed(new TestException(ORIGIN_CONSUMER));
  }

  @Test
  public void test_consumerThrowsRuntimeException_resourceClosed() throws Exception {
    test_consumerThrows_resourceClosed(new TestRuntimeException(ORIGIN_CONSUMER));
  }

  @Test
  public void test_consumerThrowsError_resourceClosed() throws Exception {
    test_consumerThrows_resourceClosed(new TestError(ORIGIN_CONSUMER));
  }

  @Test
  public void test_consumerThrowsException_and_resourceThrowsException_resourceExceptionIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(new TestException(ORIGIN_CONSUMER),
        new TestException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsRuntimeException_and_resourceThrowsException_resourceExceptionIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(
        new TestRuntimeException(ORIGIN_CONSUMER), new TestException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsError_and_resourceThrowsException_resourceExceptionIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(new TestError(ORIGIN_CONSUMER),
        new TestException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsException_and_resourceThrowsRuntimeException_resourceRuntimeExceptionIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(new TestException(ORIGIN_CONSUMER),
        new TestRuntimeException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsRuntimeException_and_resourceThrowsRuntimeException_resourceRuntimeExceptionIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(
        new TestRuntimeException(ORIGIN_CONSUMER), new TestRuntimeException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsError_and_resourceThrowsRuntimeException_resourceRuntimeExceptionIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(new TestError(ORIGIN_CONSUMER),
        new TestRuntimeException(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsException_and_resourceThrowsError_resourceErrorIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(new TestException(ORIGIN_CONSUMER),
        new TestError(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsRuntimeException_and_resourceThrowsError_resourceErrorIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(
        new TestRuntimeException(ORIGIN_CONSUMER), new TestError(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsError_and_resourceThrowsError_resourceErrorIsSuppressed()
      throws Exception {
    test_consumerThrows_and_resourceThrows_throwableIsSuppressed(new TestError(ORIGIN_CONSUMER),
        new TestError(ORIGIN_RESOURCE));
  }

  @Test
  public void test_consumerThrowsException_and_resourceThrowsException_and_consumerExceptionThrowsRuntimeException_throwableRuntimeExceptionIsThrown()
      throws Exception {
    test_consumerThrows_and_resourceThrows_and_consumerThrowableThrows_throwableThrowableIsThrown(
        new TestException(ORIGIN_CONSUMER), new TestException(ORIGIN_RESOURCE),
        new TestRuntimeException(ORIGIN_THROWABLE_ADD_SUPPRESSED));
  }

  @Test
  public void test_consumerThrowsException_and_resourceThrowsException_and_consumerExceptionThrowsError_throwableErrorIsThrown()
      throws Exception {
    test_consumerThrows_and_resourceThrows_and_consumerThrowableThrows_throwableThrowableIsThrown(
        new TestException(ORIGIN_CONSUMER), new TestException(ORIGIN_RESOURCE),
        new TestError(ORIGIN_THROWABLE_ADD_SUPPRESSED));
  }

  private void test_resourceThrowsException_resourceThrowableIsThrown(Throwable resourceThrowable)
      throws Exception {
    final TestResource resource = spy(new TestResource());
    doThrow(resourceThrowable).when(resource).close();
    final TestConsumerResult consumerResult = new TestConsumerResult();
    ResourceProducer<TestResource> producer = spy(new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    });
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return consumerResult;
          }
        });

    try {
      templateFactory.create().execute(producer, consumer);
      fail("Expected " + resourceThrowable);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(resourceThrowable)));
    }

    verify(consumer).consume(resource);
    verify(resource).close();
  }

  private void test_producerThrows_consumerNotCalled(final Throwable producerException)
      throws Exception {
    ResourceProducer<TestResource> producer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() throws Exception {
        throw throwThrowable(producerException);
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) {
            return null;
          }
        });

    try {
      templateFactory.create().execute(producer, consumer);
      fail("Expected " + producerException);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(producerException)));
    }

    verify(consumer, never()).consume(any(TestResource.class));
  }

  private void test_consumerThrows_resourceClosed(final Throwable consumerThrowable)
      throws Exception {
    final TestResource resource = spy(new TestResource());
    ResourceProducer<TestResource> producer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) throws Exception {
            throw throwThrowable(consumerThrowable);
          }
        });

    try {
      templateFactory.create().execute(producer, consumer);
      fail("Expected " + consumerThrowable);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(consumerThrowable)));
    }

    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(consumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(sameInstance(resource)));
    verify(resource).close();
  }

  private void test_consumerThrows_and_resourceThrows_throwableIsSuppressed(
      final Throwable consumerThrowable, final Throwable resourceThrowable) throws Exception {
    final TestResource resource = mock(TestResource.class);
    doThrow(resourceThrowable).when(resource).close();
    ResourceProducer<TestResource> producer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) throws Exception {
            throw throwThrowable(consumerThrowable);
          }
        });

    try {
      templateFactory.create().execute(producer, consumer);
      fail("Expected " + consumerThrowable);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(consumerThrowable)));
      final Throwable[] suppressed = consumerThrowable.getSuppressed();
      assertThat(suppressed, is(arrayWithSize(1)));
      assertThat(suppressed[0], is(sameInstance(resourceThrowable)));
    }

    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(consumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(sameInstance(resource)));
    verify(resource).close();
  }

  private void test_consumerThrows_and_resourceThrows_and_consumerThrowableThrows_throwableThrowableIsThrown(
      final Throwable consumerThrowable, final Throwable resourceThrowable,
      final Throwable throwableThrowable) throws Exception {
    final TestResource resource = mock(TestResource.class);
    doThrow(resourceThrowable).when(resource).close();

    final Throwable consumerThrowableSpy = spy(consumerThrowable);
    doThrow(throwableThrowable).when(consumerThrowableSpy).addSuppressed(resourceThrowable);

    ResourceProducer<TestResource> producer = new ResourceProducer<TestResource>() {
      @Override
      public TestResource produce() {
        return resource;
      }
    };
    ResourceConsumer<TestResource, TestConsumerResult> consumer = spy(
        new ResourceConsumer<TestResource, TestConsumerResult>() {
          @Override
          public TestConsumerResult consume(final TestResource resource) throws Exception {
            throw throwThrowable(consumerThrowableSpy);
          }
        });

    try {
      templateFactory.create().execute(producer, consumer);
      fail("Expected " + resourceThrowable);
    } catch (Throwable e) {
      assertThat(e, is(sameInstance(throwableThrowable)));
      final Throwable[] suppressed = consumerThrowable.getSuppressed();
      assertThat(suppressed, is(emptyArray()));
    }

    ArgumentCaptor<TestResource> consumerArgCaptor = ArgumentCaptor.forClass(TestResource.class);
    verify(consumer).consume(consumerArgCaptor.capture());
    assertThat(consumerArgCaptor.getValue(), is(sameInstance(resource)));
    verify(resource).close();
  }

  private static Exception throwThrowable(Throwable throwable) throws Exception {
    if (throwable instanceof Exception) {
      throw (Exception) throwable;
    }
    throw (Error) throwable;
  }

}
