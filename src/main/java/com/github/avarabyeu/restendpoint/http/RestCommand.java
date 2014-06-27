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
