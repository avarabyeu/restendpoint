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

import java.lang.reflect.Type;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;

/**
 * HTTP Message Serializer. Converts messages to/from byte array
 * 
 * @author Andrei Varabyeu
 * 
 */
public interface Serializer {

	/**
	 * Serializes Message into byte array
	 * 
	 * @param t
	 * @return
	 * @throws com.github.avarabyeu.restendpoint.http.exception.SerializerException
	 */
	<T> byte[] serialize(T t) throws SerializerException;

	/**
	 * Deserializes message from byte array
	 * 
	 * @param content
	 * @param clazz
	 * @return
	 * @throws SerializerException
	 */
	<T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException;

	/**
	 * Deserializes message from byte array
	 * 
	 * @param content
	 * @param type
	 *            - Representation of type of response. For generic types (e.g.
	 *            collections) {@link java.lang.reflect.ParameterizedType} may
	 *            be used
	 * @return
	 * @throws SerializerException
	 */
	<T> T deserialize(byte[] content, Type type) throws SerializerException;

	/**
	 * Returns MIME type of serialized messages
	 * 
	 * @return
	 */
	String getMimeType();

	/**
	 * Checks whether mime types is supported by this serializer implementation
	 * 
	 * @see http://en.wikipedia.org/wiki/Internet_media_type
	 * 
	 * @param mimeType
	 *            - MIME Type
	 * 
	 * @return TRUE if specified type is supported
	 */
	boolean canRead(MediaType mimeType);

	/**
	 * Check whether object can be serializer via this serializer implementation
	 * 
	 * @param o
	 *            - Object to be serialized
	 * @return
	 */
	boolean canWrite(Object o);
}
