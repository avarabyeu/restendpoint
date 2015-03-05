package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author Andrei Varabyeu
 */
public class VoidSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T t) throws SerializerException {
        throw new UnsupportedOperationException("Serialization is not permitted for Void types");
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        return null;
    }

    @Override
    public <T> T deserialize(byte[] content, Type type) throws SerializerException {
        return null;
    }

    @Override
    public String getMimeType() {
        throw new UnsupportedOperationException("Void type doesn't have mime type");
    }

    @Override
    public boolean canRead(MediaType mimeType, Class<?> resultType) {
        return Void.class.equals(resultType);
    }

    @Override
    public boolean canRead(MediaType mimeType, Type resultType) {
        return Void.TYPE.equals(TypeToken.of(resultType).getRawType());
    }

    @Override
    public boolean canWrite(Object o) {
        return false;
    }
}
