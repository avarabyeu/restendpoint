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

package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.http.exception.RestEndpointClientException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointServerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

/**
 * Default implementation of
 * {@link ErrorHandler}
 *
 * @author Andrei Varabyeu
 */
public class DefaultErrorHandler implements ErrorHandler<HttpUriRequest, HttpResponse> {

    /**
     * Returns TRUE in case status code of response starts with 4 or 5
     */
    @Override
    public boolean hasError(HttpResponse rs) {
        StatusType statusType = StatusType.valueOf(rs.getStatusLine().getStatusCode());
        return (statusType == StatusType.CLIENT_ERROR || statusType == StatusType.SERVER_ERROR);
    }

    /**
     * Default implementation. May be overridden in subclasses<br>
     * Throws
     * {@link com.github.avarabyeu.restendpoint.http.exception.RestEndpointClientException}
     * for client exceptions and
     * {@link com.github.avarabyeu.restendpoint.http.exception.RestEndpointServerException}
     * for server exceptions<br>
     * <p>
     * Throwed exceptions may be overridden in handle* methods
     */
    @Override
    public void handle(HttpUriRequest rq, HttpResponse rs) throws RestEndpointIOException {
        if (!hasError(rs)) {
            return;
        }
        HttpMethod httpMethod = HttpMethod.valueOf(rq.getMethod());
        URI requestUri = rq.getURI();

        StatusType statusType = StatusType.valueOf(rs.getStatusLine().getStatusCode());
        int statusCode = rs.getStatusLine().getStatusCode();
        String statusMessage = rs.getStatusLine().getReasonPhrase();

        byte[] errorBody = getErrorBody(rs);

        switch (statusType) {
            case CLIENT_ERROR:
                handleClientError(requestUri, httpMethod, statusCode, statusMessage, errorBody);
            case SERVER_ERROR:
                handleServerError(requestUri, httpMethod, statusCode, statusMessage, errorBody);
            default:
                handleDefaultError(requestUri, httpMethod, statusCode, statusMessage, errorBody);
        }
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
    protected void handleClientError(URI requestUri, HttpMethod requestMethod, int statusCode, String statusMessage,
                                     byte[] errorBody) throws RestEndpointIOException {
        throw new RestEndpointClientException(requestUri, requestMethod, statusCode, statusMessage, errorBody);
    }

    /**
     * Handler methods for HTTP server errors
     *
     * @param requestUri    - Request URI
     * @param requestMethod - Request HTTP Method
     * @param statusCode    - HTTP status code
     * @param statusMessage - HTTP status message
     * @param errorBody     - HTTP response body
     */
    protected void handleServerError(URI requestUri, HttpMethod requestMethod, int statusCode, String statusMessage,
                                     byte[] errorBody) throws RestEndpointIOException {
        throw new RestEndpointServerException(requestUri, requestMethod, statusCode, statusMessage, errorBody);
    }

    /**
     * Handler methods for unclassified errors
     *
     * @param requestUri    - Request URI
     * @param requestMethod - Request HTTP Method
     * @param statusCode    - HTTP status code
     * @param statusMessage - HTTP status message
     * @param errorBody     - HTTP response body
     */
    protected void handleDefaultError(URI requestUri, HttpMethod requestMethod, int statusCode, String statusMessage,
                                      byte[] errorBody) throws RestEndpointIOException {
        throw new RestEndpointException(requestUri, requestMethod, statusCode, statusMessage, errorBody);
    }

    /**
     * Parses byte from entity
     *
     * @param rs HTTP Response
     * @return Body as byte array
     * @throws RestEndpointIOException In case of request error
     */
    private byte[] getErrorBody(HttpResponse rs) throws RestEndpointIOException {
        HttpEntity entity = null;
        try {
            entity = rs.getEntity();
            return EntityUtils.toByteArray(rs.getEntity());
        } catch (IOException e) {
            throw new RestEndpointIOException("Unable to read body from error", e);
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
    }
}
