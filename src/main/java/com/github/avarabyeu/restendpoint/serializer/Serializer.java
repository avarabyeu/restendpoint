package com.github.avarabyeu.restendpoint.serializer;

import java.lang.reflect.Type;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;

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
	boolean canRead(String mimeType);

	/**
	 * Check whether object can be serializer via this serializer implementation
	 * 
	 * @param o
	 *            - Object to be serialized
	 * @return
	 */
	boolean canWrite(Object o);
}
