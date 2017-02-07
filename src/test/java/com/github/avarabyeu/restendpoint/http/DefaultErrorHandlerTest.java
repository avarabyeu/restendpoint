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

import com.github.avarabyeu.restendpoint.http.exception.RestEndpointException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.ByteSource;
import com.google.inject.Key;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Default Error Handler Unit Tests
 *
 * @author Andrei Varabyeu
 */
public class DefaultErrorHandlerTest {

    private ErrorHandler handler = Injector.getInstance()
            .getBean(new Key<ErrorHandler>() {
            });

    private HttpUriRequest request = Mockito.mock(HttpUriRequest.class);

    {
        Mockito.when(request.getMethod()).thenReturn("GET");
        try {
            Mockito.when(request.getURI()).thenReturn(new URI("http://google.com"));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Incorrect URI");
        }
    }

    @Test
    public void errorHandlerCheckClientError() {
        Response<ByteSource> response = getHttpResponse(404, "Not Found");
        Assert.assertTrue("Client Error is not handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckServerError() {
        Response<ByteSource> response = getHttpResponse(500, "Internal Server Error");
        Assert.assertTrue("Server Error is not handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckInformationalResponse() {
        Response<ByteSource> response = getHttpResponse(100, "Continue");
        Assert.assertFalse("Infromation response is handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckSuccessResponse() {
        Response<ByteSource> response = getHttpResponse(200, "Success");
        Assert.assertFalse("Success response is handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckRedirectionResponse() {
        Response<ByteSource> response = getHttpResponse(302, "Found");
        Assert.assertFalse("Redirection response is handled", handler.hasError(response));
    }

    @Test(expected = RestEndpointException.class)
    public void testErrorHandlerClientError() throws RestEndpointIOException {
        Response<ByteSource> response = getHttpResponse(404, "Not Found");
        handler.handle(response);
    }

    @Test(expected = RestEndpointException.class)
    public void testErrorHandlerServerError() throws RestEndpointIOException {
        Response<ByteSource> response = getHttpResponse(500, "Internal Server Error");
        handler.handle(response);
    }

    @Test
    public void testHandlerInformationalResponse() throws RestEndpointIOException {
        Response<ByteSource> response = getHttpResponse(100, "Continue");
        handler.handle(response);
    }

    @Test
    public void testErrorHandlerSuccessResponse() throws RestEndpointIOException {
        Response<ByteSource> response = getHttpResponse(200, "Success");
        handler.handle(response);
    }

    @Test
    public void testHandlerRedirectionResponse() throws RestEndpointIOException {
        Response<ByteSource> response = getHttpResponse(302, "Found");
        handler.handle(response);
    }

    private Response<ByteSource> getHttpResponse(int statusCode, String message) {
        return new Response<ByteSource>(request.getURI(), HttpMethod.valueOf(request.getMethod()), statusCode, message,
                ImmutableMultimap.<String, String>builder().build(),
                ByteSource.wrap("test string response body".getBytes(Charsets.UTF_8)));
    }
}
