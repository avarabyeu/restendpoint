package com.github.avarabyeu.restendpoint.http.proxy;

import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.Response;
import com.github.avarabyeu.restendpoint.http.RestCommand;
import com.github.avarabyeu.restendpoint.http.annotation.Body;
import com.github.avarabyeu.restendpoint.http.annotation.Path;
import com.github.avarabyeu.restendpoint.http.annotation.Query;
import com.github.avarabyeu.restendpoint.http.annotation.Request;
import com.github.avarabyeu.restendpoint.http.uri.UrlTemplate;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST interface methods parser.
 * Parses all needed meta information from annotations and builds {@link com.github.avarabyeu.restendpoint.http.RestCommand} based on it
 *
 * @author Andrei Varabyeu
 */
class RestMethodInfo {

    /* map: method argument index -> path variable */
    private final Map<Integer, String> pathArguments = new LinkedHashMap<>();

    /* HTTP method */
    private HttpMethod method;

    /* method return type is actually type of HTTP response */
    private Type responseType;

    /* whether REST method asynchronous */
    private boolean asynchronous;

    private UrlTemplate urlTemplate;

    /* body is absent by default */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Integer> bodyArgument = Optional.empty();

    /* Query parameter index */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Integer> queryParameter = Optional.empty();

    /* Whether method returns body or Response wrapper */
    private boolean returnBodyOnly;

    @Nonnull
    public static Map<Method, RestMethodInfo> mapMethods(@Nonnull Class<?> clazz) {
        Map<Method, RestMethodInfo> methodInfos = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()){
            if (RestMethodInfo.isRestMethodDefinition(method)){
                methodInfos.put(method, new RestMethodInfo(method));
            }
        }
        return methodInfos;

    }

    static boolean isRestMethodDefinition(Method m) {
        return m.isAnnotationPresent(Request.class);
    }

    static boolean isAsynchronous(Invokable<?, ?> method) {
        return Mono.class.equals(method.getReturnType().getRawType());
    }

    static boolean bodyOnly(Invokable<?, ?> method) {
        return !Response.class.equals(method.getReturnType().getRawType());
    }

    public RestMethodInfo(Method m) {
        parseMethod(Invokable.from(m));
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public boolean isBodyOnly() {
        return returnBodyOnly;
    }

    private void parseMethod(Invokable<?, ?> method) {
        Request request = method.getAnnotation(Request.class);

        this.urlTemplate = UrlTemplate.create(request.url());
        this.asynchronous = isAsynchronous(method);
        this.method = request.method();

        /* If instance wrapped with Observable, we should extract generic type parameter */
        this.responseType = asynchronous ? getGenericSubtype(method.getReturnType()) : method.getReturnType().getType();
        this.returnBodyOnly = bodyOnly(method);

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
                Preconditions.checkArgument(TypeToken.of(Map.class).isSupertypeOf(parameter.getType()),
                        "@Query parameters are expected to be maps. '%s' is not a Map", parameter.getType());
                this.queryParameter = Optional.of(i);
            }
        }

        validationPathArguments(method);
    }

    private void validationPathArguments(Invokable<?, ?> method) {
        Sets.SetView<String> difference = Sets.difference(Sets.newHashSet(urlTemplate.getPathVariables()),
                Sets.newHashSet(pathArguments.values()));
        Preconditions.checkState(difference.isEmpty(),
                "The following path arguments found in URL template, but not found in method signature: [%s]. "
                        + "Class: [%s]. Method [%s]. Did you forget @Path annotation?",
                difference.stream().collect(Collectors.joining(",")),
                method.getDeclaringClass().getSimpleName(),
                method.getName());
    }

    @SuppressWarnings("unchecked")
    private String createUrl(Object... args) {

        /* re-map method arguments. We have argIndex -> argName map, we need to build argName -> argValue map */
        final Map<String, Object> parameters = pathArguments.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, entry -> args[entry.getKey()]));

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
    public <RQ, RS> RestCommand<RQ, RS> createRestCommand(Object... args) {
        return new RestCommand(createUrl(args), this.method, createBody(args), responseType);
    }

    private Type getGenericSubtype(TypeToken<?> typeToken) {
        Type rawType = typeToken.getType();
        Preconditions.checkArgument(rawType instanceof ParameterizedType,
                "Incorrect configuration. {} should be parameterized", rawType);
        return ((ParameterizedType) rawType).getActualTypeArguments()[0];
    }

}
