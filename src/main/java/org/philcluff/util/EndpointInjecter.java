package org.philcluff.util;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import java.lang.reflect.Field;

public class EndpointInjecter {

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
}
