/**
 * 
 */
package com.github.avarabyeu.restendpoint.http.exception;

/**
 * HTTP client exception. Error code of HTTP response should starts with 5
 * 
 * @author Andrei Varabyeu
 * 
 */
public class RestEndpointClientException extends RestEndpointException {

	private static final long serialVersionUID = -6692891839503379176L;

	public RestEndpointClientException(int statusCode, String statusMessage, byte[] content) {
		super(statusCode, statusMessage, content);
	}

}
