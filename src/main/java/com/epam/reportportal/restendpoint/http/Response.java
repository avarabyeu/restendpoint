package com.epam.reportportal.restendpoint.http;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.concurrent.Immutable;
import java.net.URI;

/**
 * HTTP Response Representation
 *
 * @author Andrei Varabyeu
 */
@Immutable
public class Response<T> {

    private final URI uri;
    private final HttpMethod httpMethod;
    private final int status;
    private final String reason;
    private final Multimap<String, String> headers;
    private final T body;

    public Response(URI uri, HttpMethod httpMethod, int status, String reason,
            Multimap<String, String> headers, T body) {
        Preconditions.checkArgument(null != uri, "URL shouldn't be null or empty");
        Preconditions.checkArgument(null != httpMethod, "HttpMethod shouldn't be null or empty");
        Preconditions.checkArgument(status > 0, "Incorrect status code: %s", status);
        Preconditions.checkArgument(null != headers, "Headers shouldn't be null");

        this.uri = uri;
        this.httpMethod = httpMethod;
        this.status = status;
        this.reason = reason;
        this.headers = ImmutableMultimap.copyOf(headers);
        this.body = body;
    }

    public URI getUri() {
        return uri;
    }

    public final int getStatus() {
        return status;
    }

    public final String getReason() {
        return reason;
    }

    public final Multimap<String, String> getHeaders() {
        return headers;
    }

    public final T getBody() {
        return body;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
