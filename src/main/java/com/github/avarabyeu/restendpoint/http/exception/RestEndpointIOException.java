package com.github.avarabyeu.restendpoint.http.exception;

import java.io.IOException;

/**
 * I/O operation exceptions wrapper
 * 
 * @author Andrei Varabyeu
 * 
 */
public class RestEndpointIOException extends IOException {

	private static final long serialVersionUID = -5339772980222891685L;

	public RestEndpointIOException(String message, Throwable e) {
		super(message, e);
	}

	public RestEndpointIOException(String message) {
		super(message);
	}

}
