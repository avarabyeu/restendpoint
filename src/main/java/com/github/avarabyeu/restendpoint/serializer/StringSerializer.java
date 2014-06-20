package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

public class StringSerializer implements Serializer {

    /*
     * (non-Javadoc)
     *
     * @see
     * Serializer#serialize(java.lang.Object)
     */
    @Override
    public <T> byte[] serialize(T t) throws SerializerException {
        return t.toString().getBytes();
    }

    /*
     * (non-Javadoc)
     *
     * @see Serializer#deserialize(byte[],java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        validateString(clazz);
        return (T) new String(content);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] content, Type type) throws SerializerException {
        validateString(type);
        return (T) new String(content);
    }

    /**
     * Returns default MIME type
     */
    @Override
    public String getMimeType() {
        return MediaType.PLAIN_TEXT_UTF_8.toString();
    }

    /**
     * Checks whether mime types is supported by this serializer implementation
     */
    @Override
    public boolean canRead(String mimeType) {
        MediaType type = MediaType.parse(mimeType).withoutParameters();
        return MediaType.ANY_TEXT_TYPE.is(type) || MediaType.APPLICATION_XML_UTF_8.withoutParameters().is(type)
                || MediaType.JSON_UTF_8.withoutParameters().is(type);
    }

    @Override
    public boolean canWrite(Object o) {
        return String.class.isAssignableFrom(o.getClass());
    }

    /**
     * Validates that provided class is assignable from java.lang.String
     *
     * @param clazz Type of object to be validated
     * @throws SerializerException
     */
    private void validateString(Class<?> clazz) throws SerializerException {
        if (null != clazz && !clazz.isAssignableFrom(String.class)) {
            throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
        }
    }

    /**
     * Validates that provided type is assignable from java.lang.String
     *
     * @param type Type of object to be validated
     * @throws SerializerException
     */
    private void validateString(Type type) throws SerializerException {
        if (null != type && String.class.equals(TypeToken.of(type).getRawType())) {
            throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
        }
    }

}
