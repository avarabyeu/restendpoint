/*
 * Copyright (C) 2014 Andrei Varabyeu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * Plain String serializer
 *
 * @author avarabyeu
 * @deprecated In favor of {@link com.github.avarabyeu.restendpoint.serializer.TextSerializer}
 */
@Deprecated
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
    public boolean canRead(@Nonnull MediaType mimeType, Class<?> resultType) {
        MediaType type = mimeType.withoutParameters();
        return (type.is(MediaType.ANY_TEXT_TYPE) || MediaType.APPLICATION_XML_UTF_8.withoutParameters().is(type)
                || MediaType.JSON_UTF_8.withoutParameters().is(type)) && String.class.equals(resultType);
    }

    /**
     * Checks whether mime types is supported by this serializer implementation
     */
    @Override
    public boolean canRead(@Nonnull MediaType mimeType, Type resultType) {
        MediaType type = mimeType.withoutParameters();
        return (type.is(MediaType.ANY_TEXT_TYPE) || MediaType.APPLICATION_XML_UTF_8.withoutParameters().is(type)
                || MediaType.JSON_UTF_8.withoutParameters().is(type)) && String.class.equals(TypeToken.of(resultType).getRawType());
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
        if (null == type || !String.class.equals(TypeToken.of(type).getRawType())) {
            throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
        }
    }

}
