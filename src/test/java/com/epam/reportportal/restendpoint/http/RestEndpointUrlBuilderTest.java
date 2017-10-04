package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import com.google.common.collect.ImmutableMap;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.net.URI;

/**
 * @author Andrei Varabyeu
 */
public class RestEndpointUrlBuilderTest {

    @Test
    public void testWithPathInBaseUrl() throws RestEndpointIOException {
        HttpClientRestEndpoint endpoint = (HttpClientRestEndpoint) RestEndpoints.createDefault("http://google.com/somePath");
        URI uri = endpoint.spliceUrl("/resource", ImmutableMap.<String, String>builder().put("name", "value").build());
        SmartAssert.assertHard(uri.toString(), CoreMatchers.is("http://google.com/somePath/resource?name=value"), "Incorrect URL is built");
    }

    @Test
    public void testWithoutPathInBaseUrl() throws RestEndpointIOException {
        HttpClientRestEndpoint endpoint = (HttpClientRestEndpoint) RestEndpoints.createDefault("http://google.com");
        URI uri = endpoint.spliceUrl("/resource", ImmutableMap.<String, String>builder().put("name", "value").build());
        SmartAssert.assertHard(uri.toString(), CoreMatchers.is("http://google.com/resource?name=value"), "Incorrect URL is built");
    }

    @Test
    public void testWithoutBaseUrl() throws RestEndpointIOException {
        HttpClientRestEndpoint endpoint = (HttpClientRestEndpoint) RestEndpoints.createDefault();
        URI uri = endpoint.spliceUrl("http://google.com/resource", ImmutableMap.<String, String>builder().put("name", "value").build());
        SmartAssert.assertHard(uri.toString(), CoreMatchers.is("http://google.com/resource?name=value"), "Incorrect URL is built");
    }
}
