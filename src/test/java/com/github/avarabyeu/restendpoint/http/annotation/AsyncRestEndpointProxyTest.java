package com.github.avarabyeu.restendpoint.http.annotation;

import com.github.avarabyeu.restendpoint.http.BaseRestEndointTest;
import com.github.avarabyeu.restendpoint.http.GuiceTestModule;
import com.github.avarabyeu.restendpoint.http.Injector;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests for synchronous rest endpoint proxy methods
 *
 * @author Andrei Varabyeu
 */
public class AsyncRestEndpointProxyTest extends BaseRestEndointTest {
    private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);

    private RestInterface restInterface = Injector.getInstance().getBean(RestInterface.class);


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
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, new String(request.getBody()));

    }

    @Test
    public void testPut() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.put(String.format(SERIALIZED_STRING_PATTERN, 100, "test string"));
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "PUT / HTTP/1.1", request.getRequestLine());
        validateHeader(request);
        Assert.assertEquals("Incorrect body", SERIALIZED_STRING, new String(request.getBody()));

    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        server.enqueue(prepareResponse(SERIALIZED_STRING));
        String to = restInterface.delete();
        Assert.assertNotNull("Recieved Object is null", to);

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("Incorrect Request Line", "DELETE / HTTP/1.1", request.getRequestLine());
    }
}
