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

import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.google.common.io.ByteSource;

/**
 * Error Handler for RestEndpoint
 *
 * @author Andrei Varabyeu
 */
public interface ErrorHandler {

    /**
     * Checks whether there is an error in response
     *
     * @param rs response instance
     * @return TRUE if response contains error
     */
    boolean hasError(Response<ByteSource> rs);

    /**
     * Handles response if there is an error
     *
     * @param rs response instance
     * @throws com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException In case of error
     */
    void handle(Response<ByteSource> rs) throws RestEndpointIOException;
}
