package org.philcluff.route;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Test;
import org.philcluff.util.TestHelper;

public class SimpleRouteBuilderTest extends CamelTestSupport {

    public static final String MESSAGE = "This is a message...";
    private SimpleRouteBuilder underTest;

    @EndpointInject(uri = "mock:out") protected MockEndpoint out;
    @EndpointInject(uri = "direct:input") private DirectEndpoint in;

    @Produce protected ProducerTemplate producerTemplate;

    @Override
    protected RouteBuilder createRouteBuilder() throws IllegalAccessException {
        underTest = new SimpleRouteBuilder();
        TestHelper.injectEndpoint(underTest, "endpoint-in", in);
        TestHelper.injectEndpoint(underTest, "endpoint-out", out);
        return underTest;
    }

    @Test
    public void ukMessageIsCorrectlyRouted() throws Exception {
        out.setExpectedMessageCount(1);
        out.expectedBodiesReceived(MESSAGE);
        producerTemplate.sendBody(in, MESSAGE);
    }

    @After
    public void after() throws Exception {
        assertMockEndpointsSatisfied();
    }

}
