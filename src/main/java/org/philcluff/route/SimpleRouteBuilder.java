package org.philcluff.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class SimpleRouteBuilder extends RouteBuilder {

    public SimpleRouteBuilder(Endpoint in, Endpoint out) {
        this.in = in;
        this.out = out;
    }

    public SimpleRouteBuilder() {
    }

    @EndpointInject(ref="endpoint-in") protected Endpoint in;
    @EndpointInject(ref="endpoint-out") protected Endpoint out;

    // Simplest route builder - Consume from one endpoint, and produce to a different one.
    public void configure() {
        from(in)
                .log(LoggingLevel.WARN, "A message has been processed.")
            .to(out);
    }

}
