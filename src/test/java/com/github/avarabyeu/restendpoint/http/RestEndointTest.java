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

import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit tests for {@link com.github.avarabyeu.restendpoint.http.RestEndpoint}
 *
 * @author Andrei Varabyeu
 */
public class RestEndointTest extends BaseRestEndointTest {

    private RestEndpoint endpoint = Injector.getInstance().getBean(RestEndpoint.class);

    private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);

    @BeforeClass
    public static void before() throws IOException {
        server.play(GuiceTestModule.MOCK_PORT);
    }

    @AfterClass
    public static void after() throws IOException {
        server.shutdown();
    }

    @Test
    public void testGet() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.get("/", String.class).obtain();
        Assert.assertNotNull("Recieved Object is null", to);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "GET / HTTP/1.1", request.getRequestLine());

    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.post("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class).obtain();
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, new String(request.getBody()));

    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.put("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class).obtain();
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "PUT / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, new String(request.getBody()));

    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = endpoint.delete("/", String.class).obtain();
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "DELETE / HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void testCommand() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));

        RestCommand<String, String> command = new RestCommand<String, String>("/", HttpMethod.POST, SERIALIZED_STRING);

        String to = endpoint.executeRequest(command).obtain();

        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
    }

}
