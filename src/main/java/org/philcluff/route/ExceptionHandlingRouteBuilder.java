package org.philcluff.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.philcluff.handler.ExampleHandler;

public class ExceptionHandlingRouteBuilder extends RouteBuilder {

    @EndpointInject(ref="endpoint-in") protected Endpoint in;
    @EndpointInject(ref="endpoint-out") protected Endpoint out;
    @EndpointInject(ref="endpoint-error") protected Endpoint error;

    private ExampleHandler handler;

    public ExceptionHandlingRouteBuilder(ExampleHandler handler) {
        this.handler = handler;
    }

    // Simplest route builder - Consume from one endpoint, and produce to a different one.
    public void configure() {

        // Catch all exceptions, send to an error queue
        onException(Throwable.class)
                .handled(true)
                .to(error);

        from(in)
            .bean(handler)
            .to(out);
    }

}
