package org.philcluff.handler;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;

public class ExampleHandler {

    @Handler
    public String handle(Exchange exchange, @Body String body) {
        return body;
    }

}
