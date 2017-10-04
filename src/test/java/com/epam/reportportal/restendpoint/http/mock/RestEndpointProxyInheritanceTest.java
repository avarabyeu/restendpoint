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

import com.epam.reportportal.restendpoint.http.BaseRestEndointTest;
import com.epam.reportportal.restendpoint.http.DefaultErrorHandler;
import com.epam.reportportal.restendpoint.http.Injector;
import com.epam.reportportal.restendpoint.http.RestEndpoints;
import com.epam.reportportal.restendpoint.serializer.ByteArraySerializer;
import com.epam.reportportal.restendpoint.serializer.StringSerializer;
import com.smarttested.qa.smartassert.junit.SoftAssertVerifier;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.*;

import java.io.IOException;

/**
 * Tests for synchronous rest endpoint proxy methods
 *
 * @author Andrei Varabyeu
 */
public class RestEndpointProxyInheritanceTest extends BaseRestEndointTest {
    private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);
    private static RestInterfaceExt restInterface;

    @BeforeClass
    public static void before() throws IOException {
        server.start();

        restInterface = RestEndpoints.create().withBaseUrl("http://localhost:" + server.getPort())
                .withSerializer(new StringSerializer())
                .withSerializer(new ByteArraySerializer())
                .withErrorHandler(new DefaultErrorHandler())
                .forInterface(RestInterfaceExt.class);
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
    }

    @Test
    public void testGetExt() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.getExtended();
        Assert.assertNotNull("Recieved Object is null", to);
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "GET / HTTP/1.1", request.getRequestLine());

    }

}
