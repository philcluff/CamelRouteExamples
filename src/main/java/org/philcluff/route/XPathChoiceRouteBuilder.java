package org.philcluff.route;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;

public class XPathChoiceRouteBuilder extends RouteBuilder {

    @EndpointInject(ref="endpoint-in") protected Endpoint in;
    @EndpointInject(ref="endpoint-out-uk") protected Endpoint outUk;
    @EndpointInject(ref="endpoint-out-other") protected Endpoint outOther;

    public void configure() {

        from(in)
            .choice()
                .when(xpath("/person/city = 'London'"))
                    .log("UK message")
                    .to(outUk)
                .otherwise()
                    .log("Other message")
                    .to(outOther);
    }

}
