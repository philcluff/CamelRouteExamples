package org.philcluff.route;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

public final class TestHelper {

    // This exists to inject the correct endpoints by reference into the route
    public static void injectEndpoint(Object toInject, String ref, Endpoint endpoint) throws IllegalAccessException {
        for (Field field : toInject.getClass().getDeclaredFields()) {
            EndpointInject a = field.getAnnotation(EndpointInject.class);
            if (a != null && ref.equals(a.ref())) {
                field.setAccessible(true);
                field.set(toInject, endpoint);
                return;
            }
        }
    }

    // Used to load in static test resources
    public static String getResourceAsString(String resource) throws Exception {
        InputStream inputStream = TestHelper.class.getResourceAsStream(resource);
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        String fileContent = "";
        StringBuilder data = new StringBuilder();

        while ((fileContent = br.readLine()) != null) {
            data = data.append(fileContent);
        }

        inputStream.close();
        return data.toString();
    }


}
