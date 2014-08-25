package org.specs.pkitokens.core;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JacksonSerializer {

    private static ObjectMapper objectMapper;

    private JacksonSerializer() {
    }

    private static ObjectMapper createObjectMapper() {
        objectMapper = new ObjectMapper();

        return objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = createObjectMapper();
        }

        return objectMapper;
    }

    public static String writeValueAsString(Object value) throws IOException {
        return getObjectMapper().writeValueAsString(value);
    }

    public static <T> T readValue(String json, Class<T> valueType) throws IOException {
        return (T) getObjectMapper().readValue(json, valueType);
    }
}
