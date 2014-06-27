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

package com.github.avarabyeu.restendpoint.http.exception;

/**
 * Base HTTP error representation
 * 
 * @author Andrei Varabyeu
 * 
 */
public class RestEndpointException extends RuntimeException {

	private static final long serialVersionUID = 728718628763519460L;

	/** HTTP Status Code */
	protected int statusCode;

	/** HTTP Status Message */
	protected String statusMessage;

	/** HTTP Response Body */
	protected byte[] content;

	public RestEndpointException(int statusCode, String statusMessage, byte[] content) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("Some REST error occured\n").append("Status code: ").append(statusCode).append("\n").append("Status message: ")
				.append(statusMessage);
		return builder.toString();
	}

}
