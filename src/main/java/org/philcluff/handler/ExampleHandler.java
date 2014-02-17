package org.philcluff.handler;

import org.apache.camel.Body;
import org.apache.camel.Handler;

public class ExampleHandler {

    @Handler
    public String handle(@Body String body) throws Exception{
        return body;
    }

}
