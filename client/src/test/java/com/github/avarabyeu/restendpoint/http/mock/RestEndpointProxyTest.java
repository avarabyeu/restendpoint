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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.smarttested.qa.smartassert.junit.SoftAssertVerifier;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.*;

import java.io.IOException;
import java.util.Map;

import static com.smarttested.qa.smartassert.SmartAssert.assertSoft;
import static org.hamcrest.CoreMatchers.is;

/**
 * Tests for synchronous rest endpoint proxy methods
 *
 * @author Andrei Varabyeu
 */
public class RestEndpointProxyTest extends BaseRestEndointTest {
    private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);
    private static RestInterface restInterface;


    @BeforeClass
    public static void before() throws IOException {
        server.start();

        restInterface = RestEndpoints.create().withBaseUrl("http://localhost:" + server.getPort())
                .withSerializer(new StringSerializer())
                .withSerializer(new ByteArraySerializer())
                .withErrorHandler(new DefaultErrorHandler())
                .forInterface(RestInterface.class);
    }

    @AfterClass
    public static void after() throws IOException {
        server.shutdown();
    }

    @Rule
    public SoftAssertVerifier verifier = SoftAssertVerifier.instance();


    @Test
    public void testGet() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.get();
        Assert.assertNotNull("Recieved Object is null", to);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "GET / HTTP/1.1", request.getRequestLine());

    }

    @Test
    public void testPost() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.post(String.format(SERIALIZED_STRING_PATTERN, 100, "test string"));
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, request.getBody().readUtf8());
    }

    @Test
    public void testPostVoid() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(""));
        restInterface.postVoid(String.format(SERIALIZED_STRING_PATTERN, 100, "test string"));
        //Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, request.getBody().readUtf8());

    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.put(String.format(SERIALIZED_STRING_PATTERN, 100, "test string"));
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "PUT / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, request.getBody().readUtf8());

    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.delete();
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "DELETE / HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void testGetWithPath() throws IOException, InterruptedException {
        String somePath = "somePath";
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.getWithPath(somePath);
        Assert.assertNotNull("Recieved Object is null", to);
        RecordedRequest request = server.takeRequest();

        assertSoft(request.getRequestLine(), is("GET /somePath HTTP/1.1"), "Incorrect Request Line");
        assertSoft(request.getPath(), is("/" + somePath), "Incorrect Request Path");
    }

    @Test
    public void testGetWithQuery() throws IOException, InterruptedException {
        Map<String, String> queryParams = ImmutableMap.<String, String>builder()
                .put("param1", "value1")
                .put("param2", "value2")
                .build();

        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.getWithQuery(queryParams);
        Assert.assertNotNull("Recieved Object is null", to);
        RecordedRequest request = server.takeRequest();

        assertSoft(request.getRequestLine(), is("GET /?param1=value1&param2=value2 HTTP/1.1"), "Incorrect Request Line");
        assertSoft(request.getPath(), is("/?" + Joiner.on("&")
                .withKeyValueSeparator("=")
                .join(queryParams)), "Incorrect Request Path");

    }

    @Test
    public void testGetWithQueryNull() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.getWithQuery(null);
        Assert.assertNotNull("Recieved Object is null", to);
        RecordedRequest request = server.takeRequest();

        assertSoft(request.getRequestLine(), is("GET / HTTP/1.1"), "Incorrect Request Line");
        assertSoft(request.getPath(), is("/"), "Incorrect Request Path");
    }

}
