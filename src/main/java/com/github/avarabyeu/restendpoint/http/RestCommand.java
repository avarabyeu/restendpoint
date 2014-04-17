package com.github.avarabyeu.restendpoint.http;

import java.lang.reflect.Type;

/**
 * Base Rest request representation
 * 
 * @author Andrei Varabyeu
 * 
 * @param <RQ>
 *            - type of request
 * @param <RS>
 *            - type of response
 */
public class RestCommand<RQ, RS> {

	private HttpMethod httpMethod;
	private RQ request;
	private String uri;
	private ParameterizedTypeReference<RS> typeReference;

	public RestCommand(String uri, HttpMethod method, RQ request) {
		this.httpMethod = method;
		this.request = request;
		this.uri = uri;
		this.typeReference = new ParameterizedTypeReference<RS>() {
		};
		validate();
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public RQ getRequest() {
		return request;
	}

	public String getUri() {
		return uri;
	}

	public Type getType() {
		return typeReference.getType();
	}

	private void validate() {
		if (HttpMethod.GET.equals(this.httpMethod) && null != this.request) {
			throw new RuntimeException("'GET' request cannot contain body");
		}
	}
}
