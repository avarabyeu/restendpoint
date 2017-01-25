package com.github.avarabyeu.restendpoint.http.proxy;

import com.github.avarabyeu.restendpoint.http.HttpClientRestEndpoint;
import com.github.avarabyeu.restendpoint.http.Response;
import com.github.avarabyeu.restendpoint.http.RestEndpoint;
import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Proxy invocation handler for REST interfaces
 * Reads interface methods and caches all needed information for request execution.
 * Once method call happens, builds {@link com.github.avarabyeu.restendpoint.http.RestCommand}
 * and delegates execution to {@link com.github.avarabyeu.restendpoint.http.RestEndpoint}
 *
 * @author Andrey Vorobyov
 */
public class RestEndpointInvocationHandler implements InvocationHandler {

    public static final HttpClientRestEndpoint.BodyTransformer<Object> BODY_TRANSFORMER = new HttpClientRestEndpoint.BodyTransformer<>();
    private final Map<Method, RestMethodInfo> restMethods;

    private final RestEndpoint delegate;

    public RestEndpointInvocationHandler(Class<?> clazz, RestEndpoint restEndpoint) {
        this.delegate = restEndpoint;
        this.restMethods = RestMethodInfo.mapMethods(clazz);
    }

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executeRestMethod(method, args);
    }

    private Object executeRestMethod(Method method, Object[] args) throws Throwable {

        Preconditions
                .checkState(restMethods.containsKey(method), "Method with name [%s] is not mapped", method.getName());

        /* find appropriate method information*/
        RestMethodInfo methodInfo = restMethods.get(method);

        /* delegate request execution to RestEndpoint */
        CompletableFuture<Response<Object>> response = delegate.executeRequest(methodInfo.createRestCommand(args));

        CompletableFuture<?> result = methodInfo.isBodyOnly() ?
                response.thenApply(BODY_TRANSFORMER) :
                response;

        if (methodInfo.isAsynchronous()) {
            return result;
        } else {
            /* cannot block twice so cache it */
            //timeout?
            return result.get();
        }
    }

}
