package com.github.avarabyeu.restendpoint.http;

import com.google.common.collect.ImmutableMap;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

/**
 * @author Andrei Varabyeu
 */
public class RestEndpointUrlBuilderTest {

    @Test
    public void testWithPathInBaseUrl() throws IOException {
        HttpClientRestEndpoint endpoint = (HttpClientRestEndpoint) RestEndpoints
                .createDefault("http://google.com/somePath");
        URI uri = endpoint.spliceUrl("/resource", ImmutableMap.<String, String>builder().put("name", "value").build());
        SmartAssert.assertHard(uri.toString(), CoreMatchers.is("http://google.com/somePath/resource?name=value"),
                "Incorrect URL is built");
    }

    @Test
    public void testWithoutPathInBaseUrl() throws IOException {
        HttpClientRestEndpoint endpoint = (HttpClientRestEndpoint) RestEndpoints.createDefault("http://google.com");
        URI uri = endpoint.spliceUrl("/resource", ImmutableMap.<String, String>builder().put("name", "value").build());
        SmartAssert.assertHard(uri.toString(), CoreMatchers.is("http://google.com/resource?name=value"),
                "Incorrect URL is built");
    }

    @Test
    public void testWithoutBaseUrl() throws IOException {
        HttpClientRestEndpoint endpoint = (HttpClientRestEndpoint) RestEndpoints.createDefault();
        URI uri = endpoint.spliceUrl("http://google.com/resource",
                ImmutableMap.<String, String>builder().put("name", "value").build());
        SmartAssert.assertHard(uri.toString(), CoreMatchers.is("http://google.com/resource?name=value"),
                "Incorrect URL is built");
    }
}
