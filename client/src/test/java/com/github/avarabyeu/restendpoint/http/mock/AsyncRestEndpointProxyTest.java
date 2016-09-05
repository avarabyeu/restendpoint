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

package com.github.avarabyeu.restendpoint.http.mock;

import com.github.avarabyeu.restendpoint.http.BaseRestEndointTest;
import com.github.avarabyeu.restendpoint.http.DefaultErrorHandler;
import com.github.avarabyeu.restendpoint.http.Injector;
import com.github.avarabyeu.restendpoint.http.RestEndpoints;
import com.github.avarabyeu.restendpoint.serializer.ByteArraySerializer;
import com.github.avarabyeu.restendpoint.serializer.StringSerializer;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Tests for asynchronous client methods
 *
 * @author Andrei Varabyeu
 */
public class AsyncRestEndpointProxyTest extends BaseRestEndointTest {

    private static RestInterface restInterface;

    private static final MockWebServer serverSlow = Injector.getInstance().getBean("slow", MockWebServer.class);


    @BeforeClass
    public static void before() throws IOException {
        serverSlow.start();
        restInterface = RestEndpoints.create().withBaseUrl("http://localhost:" + serverSlow.getPort())
                .withSerializer(new StringSerializer())
                .withSerializer(new ByteArraySerializer())
                .withErrorHandler(new DefaultErrorHandler())
                .forInterface(RestInterface.class);
    }

    @AfterClass
    public static void after() throws IOException {
        serverSlow.shutdown();
    }

    @Test
    public void testGetAsync() throws IOException, InterruptedException {
        serverSlow.enqueue(prepareResponse(SERIALIZED_STRING));
        CompletableFuture<String> to = restInterface.getAsync();
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testPostAsync() throws IOException, InterruptedException {
        serverSlow.enqueue(prepareResponse(SERIALIZED_STRING));
        CompletableFuture<String> to = restInterface.postAsync(String.format(SERIALIZED_STRING_PATTERN, 100, "test string"));
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testPutAsync() throws IOException, InterruptedException {
        serverSlow.enqueue(prepareResponse(SERIALIZED_STRING));
        CompletableFuture<String> to = restInterface.putAsync(String.format(SERIALIZED_STRING_PATTERN, 100, "test string"));
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testDeleteAsync() throws IOException, InterruptedException {
        serverSlow.enqueue(prepareResponse(SERIALIZED_STRING));
        CompletableFuture<String> to = restInterface.deleteAsync();
        Assert.assertTrue(!to.isDone());
    }


}
