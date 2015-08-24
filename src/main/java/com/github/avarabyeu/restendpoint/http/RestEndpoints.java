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

import com.github.avarabyeu.restendpoint.http.proxy.RestEndpointInvocationHandler;
import com.github.avarabyeu.restendpoint.serializer.ByteArraySerializer;
import com.github.avarabyeu.restendpoint.serializer.Serializer;
import com.github.avarabyeu.restendpoint.serializer.TextSerializer;
import com.github.avarabyeu.restendpoint.serializer.json.GsonSerializer;
import com.google.common.collect.Lists;
import com.google.common.reflect.Reflection;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.util.List;

/**
 * Builders for {@link com.github.avarabyeu.restendpoint.http.RestEndpoint}
 *
 * @author avarabyeu
 */
public final class RestEndpoints {

    /**
     * No need to create instance
     */
    private RestEndpoints() {
    }

    /**
     * Creates default {@link com.github.avarabyeu.restendpoint.http.RestEndpoint} for provided endpoint URL.
     * Adds {@link com.github.avarabyeu.restendpoint.http.DefaultErrorHandler} and all possible serializers
     *
     * @return created RestEndpoint
     */
    public static RestEndpoint createDefault() {
        return new HttpClientRestEndpoint(HttpAsyncClients.createDefault(),
                Lists.newArrayList(
                        new TextSerializer(),
                        new ByteArraySerializer(), new GsonSerializer()),
                new DefaultErrorHandler());
    }

    /**
     * Creates default {@link com.github.avarabyeu.restendpoint.http.RestEndpoint} for provided endpoint URL.
     * Adds {@link com.github.avarabyeu.restendpoint.http.DefaultErrorHandler} and all possible serializers
     *
     * @param endpointUrl Base endpoint URL
     * @return created RestEndpoint
     */
    public static RestEndpoint createDefault(String endpointUrl) {
        return new HttpClientRestEndpoint(HttpAsyncClients.createDefault(),
                Lists.newArrayList(
                        new TextSerializer(),
                        new ByteArraySerializer(), new GsonSerializer()),
                new DefaultErrorHandler(),
                endpointUrl);
    }

    /**
     * Creates interface implementation (via proxy) of provided class using RestEndpoint as rest client
     * <b>Only interfaces are supported!</b>
     *
     * @param clazz    - interface to be proxied
     * @param endpoint - RestEndpoint to be used as rest client
     * @param <T>      - Type of interface to be proxied
     * @return interface implementation (e.g.) just proxy
     */
    public static <T> T forInterface(@Nonnull Class<T> clazz, RestEndpoint endpoint) {
        return Reflection.newProxy(clazz, new RestEndpointInvocationHandler(clazz, endpoint));
    }

    /**
     * Creates default builder which uses Apache Http Commons Async client as endpoint implementation
     *
     * @return New Builder instance
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Builder for {@link com.github.avarabyeu.restendpoint.http.RestEndpoint}
     */
    public static class Builder {

        private List<Serializer> serializers;

        private CloseableHttpAsyncClient httpClient;

        private HttpAsyncClientBuilder httpClientBuilder;

        private ErrorHandler<HttpUriRequest, HttpResponse> errorHandler;

        private String endpointUrl;

        Builder() {
            this.serializers = Lists.newArrayList();
            this.httpClientBuilder = HttpAsyncClientBuilder.create();
        }

        public RestEndpoint build() {
            CloseableHttpAsyncClient closeableHttpAsyncClient;
            if (null == httpClient) {
                closeableHttpAsyncClient = httpClientBuilder.build();
            } else {
                closeableHttpAsyncClient = httpClient;
            }

            return new HttpClientRestEndpoint(closeableHttpAsyncClient,
                    serializers,
                    errorHandler,
                    endpointUrl);
        }

        public Builder withBaseUrl(String url) {
            this.endpointUrl = url;
            return this;
        }

        public Builder withErrorHandler(ErrorHandler<HttpUriRequest, HttpResponse> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder withSerializer(Serializer serializer) {
            this.serializers.add(serializer);
            return this;
        }

        /**
         * Uses provided {@link org.apache.http.impl.nio.client.CloseableHttpAsyncClient}
         * <b>May override some configuration methods like {@link #withBasicAuth(String, String)}</b>
         *
         * @return this Builder
         */
        public Builder withHttpClient(CloseableHttpAsyncClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Adds Preemptive Basic authentication to the client
         *
         * @param username Username
         * @param password Password
         * @return this Builder
         */
        public Builder withBasicAuth(String username, String password) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            httpClientBuilder.addInterceptorFirst(new PreemptiveAuthInterceptor());
            return this;
        }

        public Builder withSsl(InputStream keyStore, String keyStorePass) {
            SSLContext sslcontext;
            try {
                sslcontext = SSLContexts.custom().loadTrustMaterial(IOUtils.loadKeyStore(keyStore, keyStorePass))
                        .build();
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to load trust store", e);
            }

            /*
             * Unreal magic, but we can't use
			 * org.apache.http.conn.ssl.SSLConnectionSocketFactory
			 * .BROWSER_COMPATIBLE_HOSTNAME_VERIFIER here due to some problems
			 * related to classloaders. Initialize host name verifier explicitly
			 */
            SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(
                    sslcontext,
                    new BrowserCompatHostnameVerifier());
            httpClientBuilder.setSSLStrategy(sslSessionStrategy);

            return this;
        }

        /**
         * Builds RestEndpoints and created proxy implementation for provided class
         * <b>Only interfaces are supported!</b>
         *
         * @param clazz - interface to be proxied
         * @param <T>   - type of interface to be proxied
         * @return - interface implementation based on proxy
         */
        public <T> T forInterface(@Nonnull Class<T> clazz) {
            return RestEndpoints.forInterface(clazz, build());
        }

    }

}
