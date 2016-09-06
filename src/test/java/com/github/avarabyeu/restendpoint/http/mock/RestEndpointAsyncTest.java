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
import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.Injector;
import com.github.avarabyeu.restendpoint.http.Response;
import com.github.avarabyeu.restendpoint.http.RestCommand;
import com.github.avarabyeu.restendpoint.http.RestEndpoint;
import com.github.avarabyeu.restendpoint.http.RestEndpoints;
import com.github.avarabyeu.restendpoint.serializer.ByteArraySerializer;
import com.github.avarabyeu.restendpoint.serializer.StringSerializer;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.reactivex.Observable;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests for asynchronous client methods
 *
 * @author Andrei Varabyeu
 */
public class RestEndpointAsyncTest extends BaseRestEndointTest {

    private static RestEndpoint endpoint;

    private static MockWebServer server = Injector.getInstance().getBean("slow", MockWebServer.class);

    @BeforeClass
    public static void before() throws IOException {
        server.start();
        endpoint = RestEndpoints.create().withBaseUrl("http://localhost:" + server.getPort())
                .withSerializer(new StringSerializer()).withSerializer(new ByteArraySerializer()).build();

    }

    @AfterClass
    public static void after() throws IOException {
        server.shutdown();
    }

    @Test
    public void testGet() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Observable<String> to = endpoint.getFor("/", String.class);
        Assert.assertTrue(isScheduled(to));
    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Observable<String> to = endpoint
                .postFor("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class);
        Assert.assertTrue(isScheduled(to));
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Observable<String> to = endpoint
                .putFor("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class);
        Assert.assertTrue(isScheduled(to));
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Observable<String> to = endpoint.deleteFor("/", String.class);
        Assert.assertTrue(isScheduled(to));
    }

    @Test
    public void testCommand() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        RestCommand<String, String> command = new RestCommand<>("/", HttpMethod.POST, SERIALIZED_STRING, String.class);
        Observable<Response<String>> to = endpoint.executeRequest(command);
        Assert.assertTrue(isScheduled(to));

    }

    @Test
    public void testVoid() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(""));
        Observable<Void> to = endpoint
                .postFor("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), Void.class);
        Assert.assertTrue(0 == to.count().blockingSingle());

    }

    private boolean isScheduled(Observable<?> observable) {
        return 1 == observable.count().blockingSingle();
    }
}
