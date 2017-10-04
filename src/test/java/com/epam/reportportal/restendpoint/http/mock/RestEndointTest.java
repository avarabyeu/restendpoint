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

package com.epam.reportportal.restendpoint.http.mock;

import com.epam.reportportal.restendpoint.http.*;
import com.epam.reportportal.restendpoint.serializer.ByteArraySerializer;
import com.epam.reportportal.restendpoint.serializer.StringSerializer;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Unit tests for {@link RestEndpoint}
 *
 * @author Andrei Varabyeu
 */
public class RestEndointTest extends BaseRestEndointTest {

    private static RestEndpoint endpoint;

    private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);

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
    public void testGet() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.getFor("/", String.class).blockingGet();
        Assert.assertNotNull("Received Object is null", to);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "GET / HTTP/1.1", request.getRequestLine());

    }

    @Test
    public void testGetResponse() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Response<String> to = endpoint.get("/", String.class).blockingGet();
        Assert.assertNotNull("Received Object is null", to);
        Assert.assertNotNull("Received Body is null", to);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "GET / HTTP/1.1", request.getRequestLine());

    }

    @Test
    public void testPost() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.postFor("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class)
                .blockingGet();
        Assert.assertNotNull("Received Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, request.getBody().readUtf8());

    }

    @Test
    public void testPut() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.putFor("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class).blockingGet();
        Assert.assertNotNull("Received Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "PUT / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, request.getBody().readUtf8());

    }

    @Test
    public void testDelete() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.deleteFor("/", String.class).blockingGet();
        Assert.assertNotNull("Received Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "DELETE / HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void testVoid() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(""));
        endpoint.post("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), Void.class).blockingGet();


        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, request.getBody().readUtf8());

    }

    @Test
    public void testCommand() throws IOException, InterruptedException, ExecutionException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));

        RestCommand<String, String> command = new RestCommand<String, String>("/", HttpMethod.POST, SERIALIZED_STRING, String.class);

        Response<String> to = endpoint.executeRequest(command).blockingGet();

        Assert.assertNotNull("Received Object is null", to.getBody());

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
    }

}
