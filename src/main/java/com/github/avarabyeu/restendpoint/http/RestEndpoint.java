package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;

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
    <RQ, RS> Will<RS> post(String resource, MultiPartRequest<RQ> request, Class<RS> clazz) throws RestEndpointIOException;

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
     * @param clazz    - {@link Type} of Response
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
     * @param clazz    - Response Body Type
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
