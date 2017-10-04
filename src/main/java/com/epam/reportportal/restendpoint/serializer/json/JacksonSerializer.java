package com.epam.reportportal.restendpoint.serializer.json;

import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * JSON serializer using Jackson library
 *
 * @author Andrei Varabyeu
 * @see <a href="http://wiki.fasterxml.com/JacksonHome/">Jackson Wiki</a>
 */
public class JacksonSerializer extends AbstractJsonSerializer {

    private final ObjectMapper objectMapper;

    public JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JacksonSerializer() {
        this(new ObjectMapper());
    }

    @Override
    public <T> byte[] serialize(T t) throws SerializerException {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerializerException("Unable to serialize content", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (IOException e) {
            throw new SerializerException("Unable to deserialize content", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] content, Type type) throws SerializerException {
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructType(type));
        } catch (IOException e) {
            throw new SerializerException("Unable to deserialize content", e);
        }
    }

}
