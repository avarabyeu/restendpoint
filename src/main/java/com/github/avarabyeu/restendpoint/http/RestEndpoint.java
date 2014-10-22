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

import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.github.avarabyeu.wills.Will;

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
     * @return - Response body
     * @throws com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException
     */
    <RQ, RS> Will<RS> post(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP POST method
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param type     - Type of returned response
     * @return - Response body
     * @throws RestEndpointIOException
     */
    <RQ, RS> Will<RS> post(String resource, RQ rq, Type type) throws RestEndpointIOException;

    /**
     * HTTP MultiPart POST. May contain whether serialized and binary parts
     *
     * @param resource - REST resource
     * @param request  - MultiPart request
     * @param clazz    - Type of returned response
     * @return - Response Body
     * @throws RestEndpointIOException
     */
    <RS> Will<RS> post(String resource, MultiPartRequest request, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP PUT
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param clazz    - Type of Response
     * @return - Response body
     * @throws RestEndpointIOException
     */
    <RQ, RS> Will<RS> put(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP PUT
     *
     * @param resource - REST resource
     * @param rq       - Request body
     * @param type    - {@link Type} of Response
     * @return - Response body
     * @throws RestEndpointIOException
     */
    <RQ, RS> Will<RS> put(String resource, RQ rq, Type type) throws RestEndpointIOException;

    /**
     * HTTP DELETE
     *
     * @param resource - REST Resource
     * @param clazz    - Response Body Type
     * @return - Response Body
     * @throws RestEndpointIOException
     */
    <RS> Will<RS> delete(String resource, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP GET
     *
     * @param resource - REST Resource
     * @param clazz    - Response Body Type
     * @return - Response Body
     * @throws RestEndpointIOException
     */
    <RS> Will<RS> get(String resource, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP GET
     *
     * @param resource - REST Resource
     * @param type    - Response Body Type
     * @return - Response Body
     * @throws RestEndpointIOException
     */
    <RS> Will<RS> get(String resource, Type type) throws RestEndpointIOException;

    /**
     * HTTP GET with parameters
     *
     * @param resource   - REST Resource
     * @param parameters - Map of query parameters
     * @param clazz      - Response body type
     * @return - Response Body
     * @throws RestEndpointIOException
     */
    <RS> Will<RS> get(String resource, Map<String, String> parameters, Class<RS> clazz) throws RestEndpointIOException;

    /**
     * HTTP GET with parameters
     *
     * @param resource   - REST Resource
     * @param parameters - Map of query parameters
     * @param type       - Response body type. For generic types (e.g. collections)
     *                   {@link java.lang.reflect.ParameterizedType} may be used
     * @return - Response Body
     * @throws RestEndpointIOException
     */
    <RS> Will<RS> get(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException;

    <RQ, RS> Will<RS> executeRequest(RestCommand<RQ, RS> command) throws RestEndpointIOException;
}
