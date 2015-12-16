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

/**
 * HTTP Methods Representation
 *
 * @author Andrei Varabyeu
 */
public enum HttpMethod {
    GET(false),
    POST(true),
    PUT(true),
    PATCH(true),
    DELETE(false);

    private final boolean hasBody;

    /**
     * @param hasBody Whether method contains body
     */
    HttpMethod(boolean hasBody) {
        this.hasBody = hasBody;
    }

    /**
     * @return TRUE if method contains body
     */
    boolean hasBody() {
        return hasBody;
    }
}
