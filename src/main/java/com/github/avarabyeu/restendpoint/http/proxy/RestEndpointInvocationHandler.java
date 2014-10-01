package com.github.avarabyeu.restendpoint.http.proxy;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.RestEndpoint;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Proxy invocation handler for REST interfaces
 * Reads interface methods and caches all needed information for request execution.
 * Once method call happens, builds {@link com.github.avarabyeu.restendpoint.http.RestCommand}
 * and delegates execution to {@link com.github.avarabyeu.restendpoint.http.RestEndpoint}
 *
 * @author Andrey Vorobyov
 */
public class RestEndpointInvocationHandler implements InvocationHandler {

    private Map<Method, RestMethodInfo> restMethods;

    private RestEndpoint delegate;

    public RestEndpointInvocationHandler(Class<?> clazz, RestEndpoint restEndpoint) {
        this.delegate = restEndpoint;
        this.restMethods = RestMethodInfo.mapMethods(clazz);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executeRestMethod(method, args);
    }

    private Object executeRestMethod(Method method, Object[] args) throws RestEndpointIOException {

        /* find appropriate method information*/
        RestMethodInfo methodInfo = restMethods.get(method);

        /* delegate request execution to RestEndpoint */
        Will result = delegate.executeRequest(methodInfo.createRestCommand(args));
        if (methodInfo.isAsynchronous()) {
            return result;
        } else {
            return result.obtain();
        }
    }

}
