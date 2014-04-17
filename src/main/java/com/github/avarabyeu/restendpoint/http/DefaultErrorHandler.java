package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.http.exception.RestEndpointClientException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointServerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Default implementation of
 * {@link ErrorHandler}
 *
 * @author Andrei Varabyeu
 */
public class DefaultErrorHandler implements ErrorHandler<HttpResponse> {

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
     * <p/>
     * Throwed exceptions may be overridden in handle* methods
     */
    @Override
    public void handle(HttpResponse rs) throws RestEndpointIOException {
        if (!hasError(rs)) {
            return;
        }
        StatusType statusType = StatusType.valueOf(rs.getStatusLine().getStatusCode());
        int statusCode = rs.getStatusLine().getStatusCode();
        String statusMessage = rs.getStatusLine().getReasonPhrase();
        byte[] errorBody = getErrorBody(rs);

        switch (statusType) {
            case CLIENT_ERROR:
                handleClientError(statusCode, statusMessage, errorBody);
                break;
            case SERVER_ERROR:
                handleServerError(statusCode, statusMessage, errorBody);
                break;
            default:
                handleDefaultError(statusCode, statusMessage, errorBody);
                break;
        }
    }

    /**
     * Handler methods for HTTP client errors
     *
     * @param statusCode    - HTTP status code
     * @param statusMessage - HTTP status message
     * @param errorBody     - HTTP response body
     * @throws RestEndpointIOException
     */
    protected void handleClientError(int statusCode, String statusMessage, byte[] errorBody) throws RestEndpointIOException {
        throw new RestEndpointClientException(statusCode, statusMessage, errorBody);
    }

    /**
     * Handler methods for HTTP server errors
     *
     * @param statusCode    - HTTP status code
     * @param statusMessage - HTTP status message
     * @param errorBody     - HTTP response body
     * @throws RestEndpointIOException
     */
    protected void handleServerError(int statusCode, String statusMessage, byte[] errorBody) throws RestEndpointIOException {
        throw new RestEndpointServerException(statusCode, statusMessage, errorBody);
    }

    /**
     * Handler methods for unclassified errors
     *
     * @param statusCode    - HTTP status code
     * @param statusMessage - HTTP status message
     * @param errorBody     - HTTP response body
     * @throws RestEndpointIOException
     */
    protected void handleDefaultError(int statusCode, String statusMessage, byte[] errorBody) throws RestEndpointIOException {
        throw new RestEndpointException(statusCode, statusMessage, errorBody);
    }

    /**
     * Parses byte from entity
     *
     * @param rs
     * @return
     * @throws RestEndpointIOException
     */
    private byte[] getErrorBody(HttpResponse rs) throws RestEndpointIOException {
        HttpEntity entity = null;
        try {
            entity = rs.getEntity();
            return EntityUtils.toByteArray(rs.getEntity());
        } catch (IOException e) {
            throw new RestEndpointIOException("Unable to read body from error", e);
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                throw new RestEndpointIOException("Unable to consume response entity", e);
            }
        }
    }
}
