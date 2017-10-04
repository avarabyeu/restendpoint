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

package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.http.exception.RestEndpointException;
import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import com.google.common.io.ByteSource;

import java.net.URI;

/**
 * Default implementation of
 * {@link ErrorHandler}
 *
 * @author Andrei Varabyeu
 */
public class DefaultErrorHandler implements ErrorHandler {

	/**
	 * Returns TRUE in case status code of response starts with 4 or 5
	 */
	@Override
	public boolean hasError(Response<ByteSource> rs) {
		StatusType statusType = StatusType.valueOf(rs.getStatus());
		return (statusType == StatusType.CLIENT_ERROR || statusType == StatusType.SERVER_ERROR);
	}

	/**
	 * Default implementation. May be overridden in subclasses<br>
	 * Throws
	 * {@link RestEndpointException}
	 * if Response status code starts from 4xx or 5xx
	 * <p>
	 * Throwed exceptions may be overridden in handle* methods
	 */
	@Override
	public void handle(Response<ByteSource> rs) throws RestEndpointIOException {
		if (!hasError(rs)) {
			return;
		}

		handleError(rs.getUri(), rs.getHttpMethod(), rs.getStatus(), rs.getReason(), rs.getBody());

	}

	/**
	 * Handler methods for HTTP client errors
	 *
	 * @param requestUri    - Request URI
	 * @param requestMethod - Request HTTP Method
	 * @param statusCode    - HTTP status code
	 * @param statusMessage - HTTP status message
	 * @param errorBody     - HTTP response body
	 */
	protected void handleError(URI requestUri, HttpMethod requestMethod, int statusCode, String statusMessage, ByteSource errorBody)
			throws RestEndpointIOException {
		throw new RestEndpointException(requestUri, requestMethod, statusCode, statusMessage, errorBody);
	}

}
