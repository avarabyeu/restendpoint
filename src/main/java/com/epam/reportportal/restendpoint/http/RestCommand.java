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

package com.epam.reportportal.restendpoint.http;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * Base Rest request representation
 *
 * @param <RQ> - type of request
 * @param <RS> - type of response
 * @author Andrei Varabyeu
 */
public class RestCommand<RQ, RS> {

    private final HttpMethod httpMethod;
    private final RQ request;
    private final String uri;
    private final Type responseType;
    private final boolean multipart;

    public RestCommand(@Nonnull String uri, @Nonnull HttpMethod method, @Nullable RQ request,
            @Nonnull Class<RS> responseClass) {
        this(uri, method, request, TypeToken.of(responseClass).getType(), false);
    }

    public RestCommand(@Nonnull String uri, @Nonnull HttpMethod method, @Nullable RQ request,
            @Nonnull Class<RS> responseClass, boolean multipart) {
        this(uri, method, request, TypeToken.of(responseClass).getType(), multipart);
    }

    public RestCommand(@Nonnull String uri, @Nonnull HttpMethod method, @Nullable RQ request,
            @Nonnull Type responseType, boolean multipart) {
        this.httpMethod = method;
        this.request = request;
        this.uri = uri;
        this.responseType = responseType;
        this.multipart = multipart;

        validate();
    }

    public final HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public final boolean isMultipart() {
        return multipart;
    }

    public final RQ getRequest() {
        return request;
    }

    public final String getUri() {
        return uri;
    }

    public final Type getResponseType() {
        return responseType;
    }

    private void validate() {

        /* Requests with no body should pass body parameter as NULL */
        if (!this.httpMethod.hasBody()) {
            Preconditions.checkState(null == this.request, "'%s' shouldn't contain body", this.httpMethod);
            Preconditions.checkState(!multipart, "Incorrect request type for multipart: '%s'", this.httpMethod);
        }
    }
}
