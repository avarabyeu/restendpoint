package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.RestCommand;
import com.github.avarabyeu.restendpoint.http.RestEndpoint;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by andrey.vorobyov on 17/04/14.
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
        RestCommand<String, String> command = new RestCommand<String, String>("/", HttpMethod.POST, SERIALIZED_STRING);
        Will<String> to = endpoint.executeRequest(command);
        Assert.assertTrue(!to.isDone());

    }
}
