package com.epam.reportportal.restendpoint.http.proxy;

import com.epam.reportportal.restendpoint.http.HttpMethod;
import com.epam.reportportal.restendpoint.http.MultiPartRequest;
import com.epam.reportportal.restendpoint.http.Response;
import com.epam.reportportal.restendpoint.http.RestCommand;
import com.epam.reportportal.restendpoint.http.annotation.*;
import com.epam.reportportal.restendpoint.http.uri.UrlTemplate;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import io.reactivex.Maybe;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST interface methods parser.
 * Parses all needed meta information from annotations and builds {@link RestCommand} based on it
 *
 * @author Andrei Varabyeu
 */
class RestMethodInfo {

	/* map: method argument index -> path variable */
	private final Map<Integer, String> pathArguments = new LinkedHashMap<Integer, String>();

	/* HTTP method */
	private HttpMethod method;

	/* method return type is actually type of HTTP response */
	private Type responseType;

	/* whether REST method asynchronous */
	private boolean asynchronous;

	private UrlTemplate urlTemplate;

	/* body is absent by default */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Integer> bodyArgument = Optional.absent();

	/* Query parameter index */
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Integer> queryParameter = Optional.absent();

	/* Whether method returns body or Response wrapper */
	private boolean returnBodyOnly;

	/* Whether request is multipart */
	private boolean multiPart;

	public static Map<Method, RestMethodInfo> mapMethods(Class<?> clazz) {
		Map<Method, RestMethodInfo> methodInfo = new HashMap<Method, RestMethodInfo>();
		for (Method method : clazz.getMethods()) {
			if (RestMethodInfo.isRestMethodDefinition(method)) {
				methodInfo.put(method, new RestMethodInfo(method));
			}
		}
		return methodInfo;

	}

	static boolean isRestMethodDefinition(Method m) {
		return m.isAnnotationPresent(Request.class);
	}

	static boolean isAsynchronous(Invokable<?, ?> method) {
		return Maybe.class.isAssignableFrom(method.getReturnType().getRawType());
	}

	static boolean bodyOnly(Invokable<?, ?> method) {
		return !Response.class.equals(method.getReturnType().getRawType());
	}

	static Type getResponseType(Invokable<?, ?> method) {
		Type returnType;
		if (isAsynchronous(method)) {
			final Type[] genericArgs = getGenericTypeArguments(method.getReturnType());
			if (Response.class.equals(genericArgs[0])) {
				returnType = genericArgs[1];
			} else {
				returnType = genericArgs[0];
			}

		} else if (!bodyOnly(method)) {
			final Type[] genericArgs = getGenericTypeArguments(method.getReturnType());
			returnType = genericArgs[0];
		} else {
			returnType = method.getReturnType().getType();
		}
		return returnType;
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
		this.responseType = getResponseType(method);
		this.returnBodyOnly = bodyOnly(method);

		/* walk through method parameters and find marked with internal annotations */
		ImmutableList<Parameter> methodParameters = method.getParameters();
		for (int i = 0; i < methodParameters.size(); i++) {
			Parameter parameter = methodParameters.get(i);
			if (parameter.isAnnotationPresent(Path.class)) {
				Path path = parameter.getAnnotation(Path.class);

				assert path != null;
				Preconditions.checkState(urlTemplate.hasPathVariable(path.value()),
						"There is no path parameter with name '%s' declared in url template",
						path.value()
				);
				pathArguments.put(i, path.value());
			} else if (parameter.isAnnotationPresent(Body.class)) {
				this.bodyArgument = Optional.of(i);
				if (parameter.isAnnotationPresent(Multipart.class)) {
					Preconditions.checkArgument(
							TypeToken.of(MultiPartRequest.class).isSupertypeOf(parameter.getType()),
							"@Multipart parameters are expected to be MultiPartRequest. '%s' is not a MultiPartRequest",
							parameter.getType()
					);
					multiPart = true;
				}

			} else if (parameter.isAnnotationPresent(Query.class)) {
				Preconditions.checkArgument(
						TypeToken.of(Map.class).isSupertypeOf(parameter.getType()),
						"@Query parameters are expected to be maps. '%s' is not a Map",
						parameter.getType()
				);
				this.queryParameter = Optional.of(i);
			}
		}

		validationPathArguments(method);
	}

	private void validationPathArguments(Invokable<?, ?> method) {
		Sets.SetView<String> difference = Sets.difference(Sets.newHashSet(urlTemplate.getPathVariables()),
				Sets.newHashSet(pathArguments.values())
		);
		Preconditions.checkState(difference.isEmpty(),
				"The following path arguments found in URL template, but not found in method signature: [%s]. "
						+ "Class: [%s]. Method [%s]. Did you forget @Path annotation?",
				Joiner.on(",").join(difference),
				method.getDeclaringClass().getSimpleName(),
				method.getName()
		);
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

	private Object createBody(Object... args) {
		return bodyArgument.isPresent() ? args[bodyArgument.get()] : null;
	}

	@SuppressWarnings("unchecked")
	public <RQ, RS> RestCommand<RQ, RS> createRestCommand(Object... args) {
		return new RestCommand(createUrl(args), this.method, createBody(args), responseType, multiPart);
	}

	private static Type[] getGenericTypeArguments(TypeToken<?> typeToken) {
		Type rawType = typeToken.getType();
		Preconditions.checkArgument(rawType instanceof ParameterizedType, "Incorrect configuration. {} should be parameterized", rawType);
		return ((ParameterizedType) rawType).getActualTypeArguments();
	}

}
