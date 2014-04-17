package com.github.avarabyeu.restendpoint.http;


import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.List;


/**
 * Basic Auth HTTP Client Factory
 *
 * @author Andrei Varabyeu
 */
public class BasicAuthClientFactory {

    /**
     * HTTP Credentials
     */
    private Credentials credentials;

    /*
     * Http client interceptor
     */
    private List<HttpRequestInterceptor> interceptors;

    public BasicAuthClientFactory(Credentials credentials) {
        this.credentials = credentials;
    }

    public BasicAuthClientFactory(Credentials credentials, List<HttpRequestInterceptor> interceptors) {
        this.credentials = credentials;
        this.interceptors = interceptors;
    }


    public HttpClient createHttpClient() {
        HttpClientBuilder builder = initDefaultBuilder();
        return builder.build();
    }

    /**
     * Initializes default http client builder instance
     *
     * @return
     */
    protected HttpClientBuilder initDefaultBuilder() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        builder.setDefaultCredentialsProvider(credentialsProvider);
        builder.setMaxConnPerRoute(5);
        builder.setMaxConnTotal(20);

        if (null != interceptors && !interceptors.isEmpty()) {
            for (HttpRequestInterceptor interceptor : interceptors) {
                builder.addInterceptorFirst(interceptor);
            }
        }

        return builder;
    }

}
