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

import com.github.avarabyeu.restendpoint.http.HttpMethod;

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
    private URI requestUri;

    /**
     * Request Method
     */
    private HttpMethod requestMethod;

    /**
     * HTTP Status Code
     */
    protected int statusCode;

    /**
     * HTTP Status Message
     */
    protected String statusMessage;

    /**
     * HTTP Response Body
     */
    protected byte[] content;

    public RestEndpointException(URI requestUri, HttpMethod requestMethod, int statusCode, String statusMessage, byte[] content) {
        this.requestUri = requestUri;
        this.requestMethod = requestMethod;
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
        return new StringBuilder()
                .append("Request [").append(requestMethod.toString()).append("] ")
                .append("to URL: ").append(requestUri).append(" has failed with ")
                .append("Status code: ")
                .append(statusCode).append("\n")
                .append("Status message: ")
                .append(statusMessage)
                .toString();
    }

}
