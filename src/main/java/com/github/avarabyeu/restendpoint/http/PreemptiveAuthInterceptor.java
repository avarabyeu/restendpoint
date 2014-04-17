package com.github.avarabyeu.restendpoint.http;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import java.io.IOException;


/**
 * Adds {@link org.apache.http.auth.AuthScheme} to all requests as {@link org.apache.http.client.AuthCache} object. This way
 * we are able to force {@link org.apache.http.client.HttpClient} to use auth preemptively
 */
public class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

    /**
     * Adds provided auth scheme to the client if there are no another provided
     * auth schemes
     */
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {

        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
        if (authState.getAuthScheme() == null) {

            HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            AuthCache authCache = new BasicAuthCache();
            authCache.put(targetHost, new BasicScheme());
            context.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
        }
    }
}
