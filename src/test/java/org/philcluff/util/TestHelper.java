package org.philcluff.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;

public final class TestHelper {

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
