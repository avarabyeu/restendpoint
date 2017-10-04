package com.epam.reportportal.restendpoint.http.proxy;

import com.epam.reportportal.restendpoint.http.HttpClientRestEndpoint;
import com.epam.reportportal.restendpoint.http.Response;
import com.epam.reportportal.restendpoint.http.RestCommand;
import com.epam.reportportal.restendpoint.http.RestEndpoint;
import com.google.common.base.Preconditions;
import io.reactivex.Maybe;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Proxy invocation handler for REST interfaces
 * Reads interface methods and caches all needed information for request execution.
 * Once method call happens, builds {@link RestCommand}
 * and delegates execution to {@link RestEndpoint}
 *
 * @author Andrey Vorobyov
 */
public class RestEndpointInvocationHandler implements InvocationHandler {

    public static final HttpClientRestEndpoint.BodyTransformer<Object> BODY_TRANSFORMER = new HttpClientRestEndpoint.BodyTransformer<Object>();
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

        Preconditions.checkState(restMethods.containsKey(method), "Method with name [%s] is not mapped", method.getName());

        /* find appropriate method information*/
        RestMethodInfo methodInfo = restMethods.get(method);

        /* delegate request execution to RestEndpoint */
        Maybe<Response<Object>> response = delegate.executeRequest(methodInfo.createRestCommand(args));

        Maybe<?> result = methodInfo.isBodyOnly() ? response.flatMap(BODY_TRANSFORMER) : response;

        if (methodInfo.isAsynchronous()) {
            return result;
        } else {
            /* cannot block twice so cache it */
            //timeout?
            return result.blockingGet();
        }
    }

}
