package com.github.avarabyeu.restendpoint.http.annotation;

import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.RestEndpoints;
import com.github.avarabyeu.restendpoint.serializer.StringSerializer;
import org.junit.Test;

import java.io.IOException;

/**
 * Negative tests for RestEndpoint proxy
 *
 * @author Andrei Varabyeu
 */
public class RestEndpointProxyNegativeTest {
    /**
     * Case when @Path annotation is remembered
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Test(expected = IllegalStateException.class)
    public void testGetWithPathIncorrect() throws IOException, InterruptedException {
        RestEndpoints.create()
                .withBaseUrl("http://localhost")
                .withSerializer(new StringSerializer())
                .forInterface(WrongInterface.class);
    }

    interface WrongInterface {

        @Rest(method = HttpMethod.GET, url = "/{path}")
        String getWithPathIncorrect(String path);

    }
}
