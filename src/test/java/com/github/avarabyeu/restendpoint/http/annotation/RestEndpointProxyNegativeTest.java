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

package com.github.avarabyeu.restendpoint.http.annotation;

import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.RestEndpoints;
import com.github.avarabyeu.restendpoint.serializer.StringSerializer;
import org.junit.Test;

import java.io.IOException;

/**
 * Negative tests for RestEndpoint proxy
 *
 * @author Andrei Varabyeu
 */
public class RestEndpointProxyNegativeTest {

    /**
     * Case when @Path annotation is remembered
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Test(expected = IllegalStateException.class)
    public void testGetWithPathIncorrect() throws IOException, InterruptedException {
        RestEndpoints.create()
                .withBaseUrl("http://localhost")
                .withSerializer(new StringSerializer())
                .forInterface(PathIncorrectInterface.class);
    }

    /**
     * Case when @Query parameter is not a {@link java.util.Map}
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetWithQueryIncorrect() throws IOException, InterruptedException {
        RestEndpoints.create()
                .withBaseUrl("http://localhost")
                .withSerializer(new StringSerializer())
                .forInterface(MapParameterIncorrectInterface.class);
    }

    interface PathIncorrectInterface {
        @Request(method = HttpMethod.GET, url = "/{path}")
        String getWithPathIncorrect(String path);

    }

    interface MapParameterIncorrectInterface {

        @Request(method = HttpMethod.GET, url = "/")
        String getWithQueryString(@Query String queryParams);

    }
}
