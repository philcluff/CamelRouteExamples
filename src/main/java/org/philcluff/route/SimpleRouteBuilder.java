package org.philcluff.route;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;

public class SimpleRouteBuilder extends RouteBuilder {

    private Endpoint in;
    private Endpoint outNotUk;
    private Endpoint outUk;

    public void SimpleRouteBuilder() {
        // TODO: Throw Away
    }

    public void SimpleRouteBuilder(Endpoint in, Endpoint outNotUk, Endpoint outUk) {
        this.in = in;
        this.outNotUk =  outNotUk;
        this.outUk = outUk;
    }

    public void configure() {

        // TODO: Replace with real endpoints!
        from("file:src/data?noop=true")
            .choice()
                .when(xpath("/person/city = 'London'"))
                    .log("UK message")
                    .to("file:target/messages/uk")
                .otherwise()
                    .log("Other message")
                    .to("file:target/messages/others");
    }

}
