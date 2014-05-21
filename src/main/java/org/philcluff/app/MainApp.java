package org.philcluff.app;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.philcluff.route.SimpleRouteBuilder;
import org.philcluff.util.EndpointInjecter;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainApp {

    private static final String SQS_CLIENT_NAME = "sqsClient";
    private static final String SQS_ENDPOINT_PARAMS = "?amazonSQSClient=#" + SQS_CLIENT_NAME;
    private static final String CAMEL_SQS_SCHEME = "aws-sqs";
    private static final Region SQS_REGION = Region.getRegion(Regions.EU_WEST_1);

    public static void main(String... args) throws Exception {

        Map<String, String> endpointMappings = new HashMap<>();
        endpointMappings.put("endpoint-in", "aws-sqs://a" + SQS_ENDPOINT_PARAMS);           // EG: "aws-sqs://a" OR "file:///tmp/camel/a/"
        endpointMappings.put("endpoint-out", "aws-sqs://b" + SQS_ENDPOINT_PARAMS);          // EG: "aws-sqs://b" OR "file:///tmp/camel/b/"

        SimpleRegistry reg = setupCamelRegistryForSQS();
        CamelContext context = new DefaultCamelContext(reg);

        // What I really wanted to do here was add all the routes first, then inject all endpoints in one go.
        // No such luck :(
        SimpleRouteBuilder routeBuilder = new SimpleRouteBuilder();
        injectEndpoints(endpointMappings, routeBuilder, context);
        context.addRoutes(routeBuilder);

        context.start();

        // Send a message via Producer Template to our input endpoint.
        ProducerTemplate t = context.createProducerTemplate();
        t.sendBody(endpointMappings.get("endpoint-in"), "Off we go then! " + UUID.randomUUID() + " " + new Date().toString());

        Thread.currentThread().join();
    }

    private static void injectEndpoints(Map<String, String> endpointMappings, Object route, CamelContext context) throws Exception {
        for (Map.Entry endpointReference : endpointMappings.entrySet()) {
            EndpointInjecter.injectEndpoint(route, endpointReference.getKey().toString(), getEndpointByUri(endpointReference.getValue().toString(), context));
        }
    }

    private static SimpleRegistry setupCamelRegistryForSQS() {
        AmazonSQSClient client = new AmazonSQSClient();
        client.setRegion(SQS_REGION);
        SimpleRegistry reg = new SimpleRegistry();
        reg.put(SQS_CLIENT_NAME, client);
        return reg;
    }

    private static Endpoint getEndpointByUri(String uri, CamelContext context) throws Exception {
        Component component = context.getComponent(new URI(uri).getScheme());
        if (null == component) {
            throw new UnsupportedOperationException("No such endpoint has been loaded by Camel for URI: " + uri);
        }
        return component.createEndpoint(uri);
    }

}
