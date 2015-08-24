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

import java.lang.reflect.Type;

/**
 * Byte array message converter. Actually, just placeholder to be able to work
 * with byte arrays through {@link Serializer} interface
 *
 * @author Andrei Varabyeu
 */
public class ByteArraySerializer implements Serializer {

    /*
     * (non-Javadoc)
     *
     * @see
     * Serializer#serialize(java.lang.Object)
     */
    @Override
    public final <T> byte[] serialize(T t) throws SerializerException {
        return (byte[]) t;
    }

    /*
     * (non-Javadoc)
     *
     * @see Serializer#deserialize(byte[],
     * java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        if (byte[].class.equals(clazz)) {
            return (T) content;
        }
        throw new SerializerException("Unable to deserialize to type '" + clazz.getName() + "'");
    }

    /*
     * (non-Javadoc)
     *
     * @see Serializer#deserialize(byte[],
     * java.lang.reflect.Type)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <T> T deserialize(byte[] content, Type type) throws SerializerException {
        if (byte[].class.equals(type)) {
            return (T) content;
        }
        throw new SerializerException("Unable to deserialize to type '" + type + "'");
    }

    /*
     * (non-Javadoc)
     *
     * @see Serializer#getMimeType()
     */
    @Override
    public final String getMimeType() {
        return MediaType.OCTET_STREAM.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see Serializer#canRead(java.lang.String)
     */
    @Override
    public final boolean canRead(MediaType mimeType, Class<?> resultType) {
        return mimeType.is(MediaType.ANY_TYPE) && byte[].class.equals(resultType);
    }

    @Override
    public final boolean canRead(MediaType mimeType, Type resultType) {
        return canRead(mimeType, TypeToken.of(resultType).getRawType());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * Serializer#canWrite(java.lang.Object)
     */
    @Override
    public final boolean canWrite(Object o) {
        //noinspection EqualsBetweenInconvertibleTypes
        return byte[].class.equals(o.getClass());
    }

}
