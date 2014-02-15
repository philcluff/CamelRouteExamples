package org.philcluff.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;

public class SimpleRouteBuilder extends RouteBuilder {

    @EndpointInject(ref="endpoint-in") protected Endpoint in;
    @EndpointInject(ref="endpoint-out") protected Endpoint out;

    public SimpleRouteBuilder() {
    }

    public void configure() {
        from(in)
            .to(out);
    }

}
