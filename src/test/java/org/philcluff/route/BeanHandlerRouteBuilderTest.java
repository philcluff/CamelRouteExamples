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
import org.philcluff.util.TestHelper;
import org.philcluff.handler.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanHandlerRouteBuilderTest extends CamelTestSupport {

    public static final String MESSAGE = "This is a message...";
    private BeanHandlerRouteBuilder underTest;

    @EndpointInject(uri = "mock:out") protected MockEndpoint out;
    @EndpointInject(uri = "direct:input") private DirectEndpoint in;

    @Produce protected ProducerTemplate producerTemplate;

    @Mock private ExampleHandler handler;

    @Override
    protected RouteBuilder createRouteBuilder() throws IllegalAccessException {
        underTest = new BeanHandlerRouteBuilder(handler);
        TestHelper.injectEndpoint(underTest, "endpoint-in", in);
        TestHelper.injectEndpoint(underTest, "endpoint-out", out);
        return underTest;
    }

    @Test
    public void itShouldRouteTheMessageToTheOutEndpoint() throws Exception {
        // Mock out the behaviour of ExampleHandler (It returns the message body it was passed.)
        when(handler.handle(any(Exchange.class), anyString())).thenReturn(MESSAGE);

        out.setExpectedMessageCount(1);
        out.expectedBodiesReceived(MESSAGE);
        producerTemplate.sendBody(in, MESSAGE);

        // Check that ExampleHandler got called exactly once with the inbound message.
        verify(handler, times(1)).handle(any(Exchange.class), eq(MESSAGE));
    }

    @After
    public void after() throws Exception {
        assertMockEndpointsSatisfied();
    }

}
