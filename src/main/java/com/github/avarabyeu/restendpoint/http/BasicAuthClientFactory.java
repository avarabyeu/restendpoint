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
