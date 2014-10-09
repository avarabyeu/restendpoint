package com.github.avarabyeu.restendpoint.http.proxy;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.RestCommand;
import com.github.avarabyeu.restendpoint.http.annotation.Body;
import com.github.avarabyeu.restendpoint.http.annotation.Path;
import com.github.avarabyeu.restendpoint.http.annotation.Query;
import com.github.avarabyeu.restendpoint.http.annotation.Rest;
import com.github.avarabyeu.restendpoint.http.uri.UrlTemplate;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST interface methods parser.
 * Parses all needed meta information from annotations and builds {@link com.github.avarabyeu.restendpoint.http.RestCommand} based on it
 *
 * @author Andrei Varabyeu
 */
class RestMethodInfo {

    /* map: method argument index -> path variable */
    private Map<Integer, String> pathArguments = new LinkedHashMap<Integer, String>();

    /* HTTP method */
    private HttpMethod method;

    /* method return type is actually type of HTTP response */
    private TypeToken<?> responseType;

    /* whether REST method asynchronous */
    private boolean asynchronous;

    private UrlTemplate urlTemplate;

    /* body is absent by default */
    private Optional<Integer> bodyArgument = Optional.absent();

    /* Query parameter index */
    private Optional<Integer> queryParameter = Optional.absent();

    @Nonnull
    public static Map<Method, RestMethodInfo> mapMethods(@Nonnull Class<?> clazz) {
        Map<Method, RestMethodInfo> restMethods = new LinkedHashMap<Method, RestMethodInfo>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (isRestMethodDefinition(m)) {
                restMethods.put(m, new RestMethodInfo(m));
            }
        }
        return restMethods;
    }

    static boolean isRestMethodDefinition(Method m) {
        return m.isAnnotationPresent(Rest.class);
    }

    static boolean isAsynchronous(Invokable<?, ?> method) {
        return Will.class.equals(method.getReturnType().getRawType());
    }


    public RestMethodInfo(Method m) {
        parseMethod(Invokable.from(m));
    }


    public boolean isAsynchronous() {
        return asynchronous;
    }

    private void parseMethod(Invokable<?, ?> method) {
        Rest rest = method.getAnnotation(Rest.class);

        this.urlTemplate = UrlTemplate.create(rest.url());
        this.asynchronous = isAsynchronous(method);
        this.method = rest.method();
        this.responseType = method.getReturnType();

        /* walk through method parameters and find marked with internal annotations */
        ImmutableList<Parameter> methodParameters = method.getParameters();
        for (int i = 0; i < methodParameters.size(); i++) {
            Parameter parameter = methodParameters.get(i);
            if (parameter.isAnnotationPresent(Path.class)) {
                Path path = parameter.getAnnotation(Path.class);

                assert path != null;
                Preconditions.checkState(urlTemplate.hasPathVariable(path.value()),
                        "There is no path parameter with name '%s' declared in url template", path.value());
                pathArguments.put(i, path.value());
            } else if (parameter.isAnnotationPresent(Body.class)) {
                this.bodyArgument = Optional.of(i);
            } else if (parameter.isAnnotationPresent(Query.class)) {
                Preconditions.checkArgument(TypeToken.of(Map.class).isAssignableFrom(parameter.getType()),
                        "@Query parameters are expected to be maps. '%s' is not a Map", parameter.getType());
                this.queryParameter = Optional.of(i);
            }
        }

        validationPathArguments(method);
    }

    private void validationPathArguments(Invokable<?, ?> method) {
        Sets.SetView<String> difference = Sets.difference(Sets.newHashSet(urlTemplate.getPathVariables()), Sets.newHashSet(pathArguments.values()));
        Preconditions.checkState(difference.isEmpty(), "The following path arguments found in URL template, but not found in method signature: [%s]. " +
                        "Class: [%s]. Method [%s]. Did you forget @Path annotation?", Joiner.on(',').join(difference), method.getDeclaringClass().getSimpleName(),
                method.getName());
    }

    @SuppressWarnings("unchecked")
    private String createUrl(Object... args) {

        /* re-map method arguments. We have argIndex -> argName map, we need to build argName -> argValue map */
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (Map.Entry<Integer, String> pathVariables : pathArguments.entrySet()) {
            parameters.put(pathVariables.getValue(), args[pathVariables.getKey()]);
        }
        UrlTemplate.Merger template = urlTemplate.merge().expand(parameters);
        if (queryParameter.isPresent()) {
            /* class is possible here, because we already verified arguments types before  */
            template.appendQueryParameters((Map<String, ?>) args[queryParameter.get()]);
        }
        return template.build();
    }

    @Nullable
    private Object createBody(Object... args) {
        return bodyArgument.isPresent() ? args[bodyArgument.get()] : null;
    }

    @SuppressWarnings("unchecked")
    public RestCommand<?, ?> createRestCommand(Object... args) {
        return new RestCommand(createUrl(args), this.method, createBody(args), responseType.getType());
    }

}
