package org.philcluff.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.philcluff.handler.ExampleHandler;

public class BeanHandlerRouteBuilder extends RouteBuilder {

    @EndpointInject(ref="endpoint-in") protected Endpoint in;
    @EndpointInject(ref="endpoint-out") protected Endpoint out;

    private ExampleHandler handler;

    public BeanHandlerRouteBuilder(ExampleHandler handler) {
        this.handler = handler;
    }

    // Simplest route builder - Consume from one endpoint, and produce to a different one.
    public void configure() {
        from(in)
            .bean(handler)
            .to(out);
    }

}
