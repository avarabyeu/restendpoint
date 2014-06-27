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

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Ignore;

/**
 * Base HTTP test
 *
 * @author Andrei Varabyeu
 */
@Ignore
public class BaseRestEndointTest {

    public static final String SERIALIZED_STRING = "{\"intField\":100,\"stringField\":\"test string\"}";
    public static final String SERIALIZED_STRING_PATTERN = "{\"intField\":%d,\"stringField\":\"%s\"}";

    protected void validateHeader(RecordedRequest request) {
        Assert.assertTrue(request.getHeaders().contains("Content-Type: text/plain; charset=utf-8"));
    }

    protected MockResponse prepareResponse(String body) {
        return new MockResponse().setBody(body).setHeader("Content-Type", "application/json");
    }
}
