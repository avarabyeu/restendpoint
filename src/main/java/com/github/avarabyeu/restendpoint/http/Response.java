package com.github.avarabyeu.restendpoint.http;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * HTTP Response Representation
 *
 * @author Andrei Varabyeu
 */
public class Response<T> {

    private final String url;
    private final int status;
    private final String reason;
    private final Multimap<String, String> headers;
    private final T body;

    public Response(String url, int status, String reason,
            Multimap<String, String> headers, T body) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "URL shouldn't be null or empty");
        Preconditions.checkArgument(status > 0, "Incorrect status code: %s", status);
        Preconditions.checkArgument(null != headers, "Headers shouldn't be null");

        this.url = url;
        this.status = status;
        this.reason = reason;
        this.headers = ImmutableMultimap.copyOf(headers);
        this.body = body;
    }

    public final String getUrl() {
        return url;
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
}
