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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Parameterized type reference. Implementation grabbed from SpringSource
 *
 * @author Andrei Varabyeu
 * @see <a href=
 *      "https://github.com/SpringSource/spring-framework/blob/master/spring-core/src/main/java/org/springframework/core/ParameterizedTypeReference.java"
 *      >Source</>
 *
 * @param <T>
 */
abstract public class ParameterizedTypeReference<T> {

	private final Type type;

	protected ParameterizedTypeReference() {
		Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(getClass());
		Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();

		if (!ParameterizedType.class.isInstance(type)) {
			throw new IllegalArgumentException("");
		}
		// Assert.isInstanceOf(ParameterizedType.class, type);
		ParameterizedType parameterizedType = (ParameterizedType) type;
		// Assert.isTrue(parameterizedType.getActualTypeArguments().length ==
		// 1);
		this.type = parameterizedType.getActualTypeArguments()[0];
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof ParameterizedTypeReference && this.type.equals(((ParameterizedTypeReference<?>) obj).type)));
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public String toString() {
		return "ParameterizedTypeReference<" + this.type + ">";
	}

	private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
		Class<?> parent = child.getSuperclass();
		if (Object.class.equals(parent)) {
			throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
		} else if (ParameterizedTypeReference.class.equals(parent)) {
			return child;
		} else {
			return findParameterizedTypeReferenceSubclass(parent);
		}
	}

}
