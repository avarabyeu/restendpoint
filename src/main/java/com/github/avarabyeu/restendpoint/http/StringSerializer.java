package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StringSerializer implements Serializer {

    /**
     * List of supported MIME types. It will be good to introduce encoding here,
     * but first implementation uses UTF-8 only <br>
     * TODO: Add encoding handling here. <br>
     * TODO: Support not only JSON mime types
     */
    private List<String> MIME_TYPES = new ArrayList<String>() {
        private static final long serialVersionUID = 7394081878944516157L;

        {
            add("application/json");
            add("application/+json");
            add("application/json;charset=UTF-8");
        }
    };

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
        return MIME_TYPES.get(0);
    }

    /**
     * Checks whether mime types is supported by this serializer implementation
     */
    @Override
    public boolean canRead(String mimeType) {
        return MIME_TYPES.contains(mimeType);
    }

    @Override
    public boolean canWrite(Object o) {
        return String.class.isAssignableFrom(o.getClass());
    }

    /**
     * Validates that provided class is assignable from java.lang.String
     *
     * @param clazz
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
     * @param type
     * @throws SerializerException
     */
    private void validateString(Type type) throws SerializerException {
        if (null != type && String.class.equals(type)) {
            throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
        }
    }

}
