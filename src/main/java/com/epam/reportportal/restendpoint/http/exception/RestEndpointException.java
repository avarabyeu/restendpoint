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

package com.epam.reportportal.restendpoint.http.exception;

import com.epam.reportportal.restendpoint.http.HttpMethod;
import com.google.common.io.ByteSource;

import java.net.URI;

/**
 * Base HTTP error representation
 *
 * @author Andrei Varabyeu
 */
public class RestEndpointException extends RuntimeException {

	private static final long serialVersionUID = 728718628763519460L;

	/**
	 * Request URI
	 */
	private final URI requestUri;

	/**
	 * Request Method
	 */
	private final HttpMethod requestMethod;

	/**
	 * HTTP Status Code
	 */
	private final int statusCode;

	/**
	 * HTTP Status Message
	 */
	private final String statusMessage;

	/**
	 * HTTP Response Body
	 */
	private final ByteSource content;

	public RestEndpointException(URI requestUri, HttpMethod requestMethod, int statusCode, String statusMessage, ByteSource content) {
		this.requestUri = requestUri;
		this.requestMethod = requestMethod;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.content = content;
	}

	public URI getRequestUri() {
		return requestUri;
	}

	public HttpMethod getRequestMethod() {
		return requestMethod;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public ByteSource getContent() {
		return content;
	}

	/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Throwable#getMessage()
		 */
	@Override
	public String getMessage() {
		return new StringBuilder().append("Request [")
				.append(requestMethod.toString())
				.append("] ")
				.append("to URL: ")
				.append(requestUri)
				.append(" has failed with ")
				.append("Status code: ")
				.append(statusCode)
				.append('\n')
				.append("Status message: ")
				.append(statusMessage)
				.append('\n')
				.append("Content: '")
				.append(content)
				.append('\'')
				.toString();
	}

}
