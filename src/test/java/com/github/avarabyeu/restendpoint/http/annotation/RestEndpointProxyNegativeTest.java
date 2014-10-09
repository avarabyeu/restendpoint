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
                .forInterface(PathIncorrectInterface.class);
    }

    /**
     * Case when @Query parameter is not a {@link java.util.Map}
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetWithQueryIncorrect() throws IOException, InterruptedException {
        RestEndpoints.create()
                .withBaseUrl("http://localhost")
                .withSerializer(new StringSerializer())
                .forInterface(MapParameterIncorrectInterface.class);
    }

    interface PathIncorrectInterface {
        @Rest(method = HttpMethod.GET, url = "/{path}")
        String getWithPathIncorrect(String path);

    }

    interface MapParameterIncorrectInterface {

        @Rest(method = HttpMethod.GET, url = "/")
        String getWithQueryString(@Query String queryParams);

    }
}
