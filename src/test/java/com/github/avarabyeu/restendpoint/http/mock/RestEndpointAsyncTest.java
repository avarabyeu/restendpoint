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

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.*;
import com.squareup.okhttp.mockwebserver.MockWebServer;
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

    private RestEndpoint endpoint = Injector.getInstance().getBean(RestEndpoint.class);

    private static MockWebServer server = Injector.getInstance().getBean("slow", MockWebServer.class);

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
        Will<String> to = endpoint.get("/", String.class);
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Will<String> to = endpoint.post("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class);
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Will<String> to = endpoint.put("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class);
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        Will<String> to = endpoint.delete("/", String.class);
        Assert.assertTrue(!to.isDone());
    }

    @Test
    public void testCommand() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        RestCommand<String, String> command = new RestCommand<String, String>("/", HttpMethod.POST, SERIALIZED_STRING, String.class);
        Will<String> to = endpoint.executeRequest(command);
        Assert.assertTrue(!to.isDone());

    }
}
