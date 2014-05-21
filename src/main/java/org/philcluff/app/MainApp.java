package org.philcluff.app;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.philcluff.route.SimpleRouteBuilder;
import org.philcluff.util.EndpointInjecter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MainApp {

    private final static String SQS_CLIENT_NAME = "sqsClient";
    private final static String SQS_ENDPOINT_PARAMS = "?amazonSQSClient=#" + SQS_CLIENT_NAME;
    private final static Region SQS_REGION = Region.getRegion(Regions.EU_WEST_1);
    public static final String CAMEL_SQS_SCHEME = "aws-sqs";

    public static void main(String... args) throws Exception {

        Map<String, String> endpointMappings = new HashMap<>();
        endpointMappings.put("endpoint-in", "aws-sqs://a");           // EG: "aws-sqs://a" OR "file:///tmp/camel/a/"
        endpointMappings.put("endpoint-out", "file:///tmp/camel/b/"); // EG: "aws-sqs://b" OR "file:///tmp/camel/b/"

        SimpleRegistry reg = setupCamelRegistryForSQS();
        CamelContext context = new DefaultCamelContext(reg);

        // What I really wanted to do here was add all the routes first, then inject all endpoints in one go.
        // No such luck :(
        SimpleRouteBuilder routeBuilder = new SimpleRouteBuilder();
        injectEndpoints(endpointMappings, routeBuilder, context);
        context.addRoutes(routeBuilder);

        context.start();
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
        if (uri.startsWith(CAMEL_SQS_SCHEME)) {
            return context.getComponent(CAMEL_SQS_SCHEME).createEndpoint(uri + SQS_ENDPOINT_PARAMS);
        }
        else {
            return context.getComponent(new URI(uri).getScheme()).createEndpoint(uri);
        }
    }

}
