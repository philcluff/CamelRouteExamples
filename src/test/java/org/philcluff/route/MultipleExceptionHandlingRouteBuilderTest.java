package org.philcluff.route;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.philcluff.handler.ExampleHandler;
import org.philcluff.util.TestHelper;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultipleExceptionHandlingRouteBuilderTest extends CamelTestSupport {

    public static final String MESSAGE = "This is a message...";
    private MultipleExceptionHandlingRouteBuilder underTest;

    @EndpointInject(uri = "mock:out") protected MockEndpoint out;
    @EndpointInject(uri = "mock:error") protected MockEndpoint error;
    @EndpointInject(uri = "mock:ioerror") protected MockEndpoint ioError;
    @EndpointInject(uri = "direct:input") private DirectEndpoint in;

    @Produce protected ProducerTemplate producerTemplate;

    @Mock private ExampleHandler handler;

    @Override
    protected RouteBuilder createRouteBuilder() throws IllegalAccessException {
        underTest = new MultipleExceptionHandlingRouteBuilder(handler);
        TestHelper.injectEndpoint(underTest, "endpoint-in", in);
        TestHelper.injectEndpoint(underTest, "endpoint-out", out);
        TestHelper.injectEndpoint(underTest, "endpoint-io-error", ioError);
        TestHelper.injectEndpoint(underTest, "endpoint-other-error", error);
        return underTest;
    }

    @Test
    public void itShouldRouteTheMessageToTheOutEndpoint() throws Exception {
        when(handler.handle(anyString())).thenReturn(MESSAGE);
        out.setExpectedMessageCount(1);
        error.setExpectedMessageCount(0);
        out.expectedBodiesReceived(MESSAGE);
        producerTemplate.sendBody(in, MESSAGE);
        verify(handler, times(1)).handle(eq(MESSAGE));
    }

    @Test
    public void itShouldProduceToGenericErrorEndpointOnException() throws Exception {
        when(handler.handle(anyString())).thenThrow(new RuntimeException("Oh noes!"));
        out.setExpectedMessageCount(0);
        error.setExpectedMessageCount(1);
        ioError.setExpectedMessageCount(0);

        try {
            producerTemplate.sendBody(in, MESSAGE);
        }
        catch (Exception e) {
            fail("Route should handle exception.");
        }
        verify(handler, times(1)).handle(eq(MESSAGE));
    }

    @Test
    public void itShouldProduceToIOErrorEndpointOnIOException() throws Exception {
        when(handler.handle(anyString())).thenThrow(new IOException("An IO Exception happened!"));
        out.setExpectedMessageCount(0);
        error.setExpectedMessageCount(0);
        ioError.setExpectedMessageCount(1);

        try {
            producerTemplate.sendBody(in, MESSAGE);
        }
        catch (Exception e) {
            fail("Route should handle exception.");
        }
        verify(handler, times(1)).handle(eq(MESSAGE));
    }

    @After
    public void after() throws Exception {
        assertMockEndpointsSatisfied();
    }

}
