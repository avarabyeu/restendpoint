package com.github.avarabyeu.restendpoint.http.exception;

/**
 * Serializer Exception. <br>
 * Throwed from {@link com.github.avarabyeu.restendpoint.serializer.Serializer}
 * implementations
 * 
 * @author Andrei Varabyeu
 * 
 */
public class SerializerException extends RestEndpointIOException {

	private static final long serialVersionUID = 1L;

	public SerializerException(String message) {
		super(message);
	}

	public SerializerException(String message, Throwable e) {
		super(message, e);
	}
}
