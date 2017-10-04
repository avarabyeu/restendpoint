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

import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import io.reactivex.Maybe;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Interface of endpoint of REST web service
 *
 * @author Andrei Varabyeu
 */
public interface RestEndpoint {

    /**
     * HTTP POST method
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param clazz    - Type of returned response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<Response<RS>> post(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP POST method. Returns body only
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param clazz    - Type of returned response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Response body
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<RS> postFor(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP POST method
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param type     - Type of returned response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<Response<RS>> post(String resource, RQ rq, Type type) throws RestEndpointIOException;

    /**
     * HTTP POST method. Returns body only
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param type     - Type of returned response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Response body
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<RS> postFor(String resource, RQ rq, Type type) throws RestEndpointIOException;

    /**
     * HTTP MultiPart POST. May contain whether serialized and binary parts
     *
     * @param resource - REST resource
     * @param request  - MultiPart request
     * @param clazz    - Type of returned response
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<Response<RS>> post(String resource, MultiPartRequest request, Class<RS> clazz)
            throws RestEndpointIOException;

    /**
     * HTTP MultiPart POST. May contain whether serialized and binary parts. Returns body only
     *
     * @param resource - REST resource
     * @param request  - MultiPart request
     * @param clazz    - Type of returned response
     * @param <RS>     - Type of Response
     * @return - Response Body
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<RS> postFor(String resource, MultiPartRequest request, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP PUT
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param clazz    - Type of Response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<Response<RS>> put(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP PUT. Returns body only
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param clazz    - Type of Response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Response body
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<RS> putFor(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP PUT
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param type     - {@link Type} of Response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<Response<RS>> put(String resource, RQ rq, Type type) throws RestEndpointIOException;

    /**
     * HTTP PUT. Returns body only
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param type     - {@link Type} of Response
     * @param <RQ>     - Type of Request
     * @param <RS>     - Type of Response
     * @return - Response body
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<RS> putFor(String resource, RQ rq, Type type) throws RestEndpointIOException;

    /**
     * HTTP DELETE
     *
     * @param resource - REST Resource
     * @param clazz    - Response Body Type
     * @param <RS>     - Type of Response
     * @return Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<Response<RS>> delete(String resource, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP DELETE. Returns body only
     *
     * @param resource - REST Resource
     * @param clazz    - Response Body Type
     * @param <RS>     - Type of Response
     * @return - Response Body
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<RS> deleteFor(String resource, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP GET
     *
     * @param resource - REST Resource
     * @param clazz    - Response Body Type
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<Response<RS>> get(String resource, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP GET. Returns body only
     *
     * @param resource - REST Resource
     * @param clazz    - Response Body Type
     * @param <RS>     - Type of Response
     * @return - Response Body
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<RS> getFor(String resource, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP GET
     *
     * @param resource - REST Resource
     * @param type     - Response Body Type
     * @param <RS>     - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<Response<RS>> get(String resource, Type type) throws RestEndpointIOException;

    /**
     * HTTP GET. Returns body only
     *
     * @param <RS>     - Type of Response
     * @param resource - REST Resource
     * @param type     - Response Body Type
     * @return - Response Body
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<RS> getFor(String resource, Type type) throws RestEndpointIOException;

    /**
     * HTTP GET with parameters
     *
     * @param resource   - REST Resource
     * @param parameters - Map of query parameters
     * @param clazz      - Response body type
     * @param <RS>       - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<Response<RS>> get(String resource, Map<String, String> parameters, Class<RS> clazz)
            throws RestEndpointIOException;

    /**
     * HTTP GET with parameters. Returns body only
     *
     * @param resource   - REST Resource
     * @param parameters - Map of query parameters
     * @param clazz      - Response body type
     * @param <RS>       - Type of Response
     * @return - Response Body
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<RS> getFor(String resource, Map<String, String> parameters, Class<RS> clazz)
            throws RestEndpointIOException;

    /**
     * HTTP GET with parameters
     *
     * @param resource   - REST Resource
     * @param parameters - Map of query parameters
     * @param type       - Response body type. For generic types (e.g. collections)
     *                   {@link java.lang.reflect.ParameterizedType} may be used
     * @param <RS>       - Type of Response
     * @return - Wrapped Response
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<Response<RS>> get(String resource, Map<String, String> parameters, Type type)
            throws RestEndpointIOException;

    /**
     * HTTP GET with parameters. Returns body only
     *
     * @param resource   - REST Resource
     * @param parameters - Map of query parameters
     * @param type       - Response body type. For generic types (e.g. collections)
     *                   {@link java.lang.reflect.ParameterizedType} may be used
     * @param <RS>       - Type of Response
     * @return - Response Body
     * @throws RestEndpointIOException In case of error
     */
    <RS> Maybe<RS> getFor(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException;

    /**
     * General method for executing HTTP requests
     *
     * @param command HTTP request representation
     * @param <RQ>    Type of Request Body
     * @param <RS>    Type of Response Body
     * @return Response object
     * @throws RestEndpointIOException In case of error
     */
    <RQ, RS> Maybe<Response<RS>> executeRequest(RestCommand<RQ, RS> command) throws RestEndpointIOException;
}
