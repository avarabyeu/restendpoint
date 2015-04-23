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

import java.lang.reflect.Type;

/**
 * HTTP Message Serializer. Converts messages to/from byte array
 *
 * @author Andrei Varabyeu
 */
public interface Serializer {

    /**
     * Serializes Message into byte array
     *
     * @param t   Object to be serialized
     * @param <T> Type of object to be serialized
     * @return serialized object as byte array
     * @throws SerializerException In case of some exception
     */
    <T> byte[] serialize(T t) throws SerializerException;

    /**
     * Deserializes message from byte array
     *
     * @param content Object to be deserialized
     * @param clazz   Result Type
     * @param <T>     Type of deserialization result
     * @return Deserialized Object
     * @throws SerializerException In case of some exception
     */
    <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException;

    /**
     * Deserializes message from byte array
     *
     * @param content content to be deserialized
     * @param type    - Representation of type of response. For generic types (e.g.
     *                collections) {@link java.lang.reflect.ParameterizedType} may
     *                be used
     * @param <T>     Type of result object
     * @return Deserialized Object
     * @throws SerializerException In case of some exception
     */
    <T> T deserialize(byte[] content, Type type) throws SerializerException;

    /**
     * Returns MIME type of serialized messages
     *
     * @return MIME type of serialized messages
     */
    String getMimeType();

    /**
     * Checks whether mime types is supported by this serializer implementation
     *
     * @param resultType - Result Type
     * @param mimeType   - MIME Type
     * @return TRUE if specified type is supported
     * @see <a href="http://en.wikipedia.org/wiki/Internet_media_type">Internet Media Type</a>
     */
    boolean canRead(MediaType mimeType, Class<?> resultType);

    /**
     * Checks whether mime types is supported by this serializer implementation
     *
     * @param mimeType   - MIME Type
     * @param resultType - Representation of type of response. For generic types (e.g.
     *                   collections) {@link java.lang.reflect.ParameterizedType} may
     *                   be used
     * @return TRUE if specified type is supported
     * @see <a href="http://en.wikipedia.org/wiki/Internet_media_type">Internet Media Type</a>
     */
    boolean canRead(MediaType mimeType, Type resultType);

    /**
     * Check whether object can be serializer via this serializer implementation
     *
     * @param o - Object to be serialized
     * @return TRUE if serializer can serialize provided object
     */
    boolean canWrite(Object o);
}
