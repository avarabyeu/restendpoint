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

package com.epam.reportportal.restendpoint.http.annotation;

import com.epam.reportportal.restendpoint.http.HttpMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Method of REST client definition
 *
 * @author Andrey Vorobyov
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Request {

    /**
     * HTTP method type
     */
    HttpMethod method();

    /**
     * URL template. Usage example: "/api/resource/{id}".
     * So, placeholders in form '{}' are acceptable
     */
    String url();
}
