package com.github.avarabyeu.restendpoint.http.exception;

/**
 * HTTP Server Error Representation. Exception should starts with 4
 * 
 * @author Andrei Varabyeu
 * 
 */
public class RestEndpointServerException extends RestEndpointException {

	private static final long serialVersionUID = -7422405783931746961L;

	public RestEndpointServerException(int statusCode, String statusMessage, byte[] content) {
		super(statusCode, statusMessage, content);
	}

}
