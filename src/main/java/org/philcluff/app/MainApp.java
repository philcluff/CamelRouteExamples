package org.philcluff.app;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.component.aws.sqs.SqsComponent;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.philcluff.route.SimpleRouteBuilder;

public class MainApp {

    private final static String SQS_CLIENT_NAME = "sqsClient";
    private final static String SQS_ENDPOINT_PARAMS = "?amazonSQSClient=#" + SQS_CLIENT_NAME;
    private final static Region SQS_REGION = Region.getRegion(Regions.EU_WEST_1);

    public static void main(String... args) throws Exception {

        // "aws-sqs://a" OR "file:///tmp/camel/a/"
        String inputEndpointUri = "aws-sqs://a";
        // "aws-sqs://b" OR "file:///tmp/camel/b/"
        String outputEndpointUri = "file:///tmp/camel/b/";

        SimpleRegistry reg = setupCamelRegistryForSQS();
        CamelContext context = getCamelContext(reg);

        Endpoint inputEndpoint = getEndpointByUri(inputEndpointUri, context);
        Endpoint outputEndpoint = getEndpointByUri(outputEndpointUri, context);

        context.addRoutes(new SimpleRouteBuilder(inputEndpoint, outputEndpoint));
        context.start();
        Thread.currentThread().join();

    }

    private static SimpleRegistry setupCamelRegistryForSQS() {
        AmazonSQSClient client = new AmazonSQSClient();
        client.setRegion(SQS_REGION);
        SimpleRegistry reg = new SimpleRegistry();
        reg.put(SQS_CLIENT_NAME, client);
        return reg;
    }

    private static CamelContext getCamelContext(SimpleRegistry reg) {
        CamelContext context = new DefaultCamelContext(reg);
        Component fileComponent = new FileComponent();
        Component sqsComponent = new SqsComponent();
        fileComponent.setCamelContext(context);
        sqsComponent.setCamelContext(context);
        return context;
    }

    private static Endpoint getEndpointByUri(String uri, CamelContext context) throws Exception {
        if (uri.startsWith("aws-sqs")) {
            return context.getComponent("aws-sqs").createEndpoint(uri + SQS_ENDPOINT_PARAMS);
        }
        else if (uri.startsWith("file")) {
            return context.getComponent("file").createEndpoint(uri);
        }
        return null;
    }

}
