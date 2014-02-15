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

public class XPathChoiceRouteBuilderTest extends CamelTestSupport {

    private XPathChoiceRouteBuilder underTest;

    @EndpointInject(uri = "mock:outUk") protected MockEndpoint outUk;
    @EndpointInject(uri = "mock:outOther") protected MockEndpoint outOther;
    @EndpointInject(uri = "direct:input") private DirectEndpoint in;

    @Produce protected ProducerTemplate producerTemplate;

    @Override
    protected RouteBuilder createRouteBuilder() throws IllegalAccessException {
        underTest = new XPathChoiceRouteBuilder();
        TestHelper.injectEndpoint(underTest, "endpoint-in", in);
        TestHelper.injectEndpoint(underTest, "endpoint-out-uk", outUk);
        TestHelper.injectEndpoint(underTest, "endpoint-out-other", outOther);
        return underTest;
    }

    @After
    public void after() throws Exception {
        assertMockEndpointsSatisfied();
    }

    @Test
    public void ukMessageIsCorrectlyRouted() throws Exception {
        outUk.setExpectedMessageCount(1);
        outOther.setExpectedMessageCount(0);
        outUk.expectedBodiesReceived(getUkMessage());
        producerTemplate.sendBody(in, getUkMessage());
    }

    @Test
    public void nonUkMessageIsCorrectlyRouted() throws Exception {
        outOther.setExpectedMessageCount(1);
        outUk.setExpectedMessageCount(0);
        outOther.expectedBodiesReceived(getNonUkMessage());
        producerTemplate.sendBody(in, getNonUkMessage());
    }

    private String getUkMessage() throws Exception {
        return TestHelper.getResourceAsString("/ukMessage.xml");
    }

    private String getNonUkMessage() throws Exception {
        return TestHelper.getResourceAsString("/nonUkMessage.xml");
    }

}
