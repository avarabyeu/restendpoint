package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;

/**
 * Error Handler for RestEndpoint
 * 
 * @author Andrei Varabyeu
 * 
 * @param <RS>
 *            - Type of Response
 */
public interface ErrorHandler<RS> {

	/**
	 * Checks whether there is an error in response
	 * 
	 * @param rs
	 * @return
	 */
	boolean hasError(RS rs);

	/**
	 * Handles response if there is an error
	 * 
	 * @param rs
	 * @throws com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException
	 */
	void handle(RS rs) throws RestEndpointIOException;
}
