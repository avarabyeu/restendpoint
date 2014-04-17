package com.github.avarabyeu.restendpoint.http.endpoint;

import com.github.avarabyeu.restendpoint.http.ErrorHandler;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointClientException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointServerException;
import com.google.inject.Key;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * Default Error Handler Unit Tests
 *
 * @author Andrei Varabyeu
 */
public class DefaultErrorHandlerTest {

    private ErrorHandler<HttpResponse> handler = Injector.getInstance().getBean(new Key<ErrorHandler<HttpResponse>>() {
    });

    @Test
    public void errorHandlerCheckClientError() {
        HttpResponse response = getHttpResponse(404, "Not Found");
        Assert.assertTrue("Client Error is not handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckServerError() {
        HttpResponse response = getHttpResponse(500, "Internal Server Error");
        Assert.assertTrue("Server Error is not handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckInformationalResponse() {
        HttpResponse response = getHttpResponse(100, "Continue");
        Assert.assertFalse("Infromation response is handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckSuccessResponse() {
        HttpResponse response = getHttpResponse(200, "Success");
        Assert.assertFalse("Success response is handled", handler.hasError(response));
    }

    @Test
    public void errorHandlerCheckRedirectionResponse() {
        HttpResponse response = getHttpResponse(302, "Found");
        Assert.assertFalse("Redirection response is handled", handler.hasError(response));
    }

    @Test(expected = RestEndpointClientException.class)
    public void testErrorHandlerClientError() throws RestEndpointIOException {
        HttpResponse response = getHttpResponse(404, "Not Found");
        handler.handle(response);
    }

    @Test(expected = RestEndpointServerException.class)
    public void testErrorHandlerServerError() throws RestEndpointIOException {
        HttpResponse response = getHttpResponse(500, "Internal Server Error");
        handler.handle(response);
    }

    @Test
    public void testHandlerInformationalResponse() throws RestEndpointIOException {
        HttpResponse response = getHttpResponse(100, "Continue");
        handler.handle(response);
    }

    @Test
    public void testErrorHandlerSuccessResponse() throws RestEndpointIOException {
        HttpResponse response = getHttpResponse(200, "Success");
        handler.handle(response);
    }

    @Test
    public void testHandlerRedirectionResponse() throws RestEndpointIOException {
        HttpResponse response = getHttpResponse(302, "Found");
        handler.handle(response);
    }

    private HttpResponse getHttpResponse(int statusCode, String message) {
        StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, message);
        BasicHttpResponse response = new BasicHttpResponse(statusLine, EnglishReasonPhraseCatalog.INSTANCE, Locale.US);
        response.setEntity(new StringEntity("test string response body", Consts.UTF_8));
        return response;
    }
}
