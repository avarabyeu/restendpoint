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
import com.epam.reportportal.restendpoint.http.Injector;
import com.epam.reportportal.restendpoint.http.RestEndpoint;
import com.epam.reportportal.restendpoint.http.RestEndpoints;
import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import com.epam.reportportal.restendpoint.serializer.ByteArraySerializer;
import com.epam.reportportal.restendpoint.serializer.StringSerializer;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import io.reactivex.Maybe;
import org.apache.commons.codec.binary.Base64;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * {@link RestEndpoints} tests
 *
 * @author avarabyeu
 */
public class RestEndpointsTest extends BaseRestEndointTest {

	public static final String HTTP_TEST_URK = "http://localhost:";
	public static final String ECHO_STRING = "Hello world!";
	public static final String RESOURCE = "/";

	private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);

	@BeforeClass
	public static void before() throws IOException {
		server.start();
	}

	@AfterClass
	public static void after() throws IOException {
		server.shutdown();
	}

	@Test
	public void testDefault() throws RestEndpointIOException, ExecutionException, InterruptedException {
		RestEndpoint endpoint = RestEndpoints.createDefault(HTTP_TEST_URK + server.getPort());
		Assert.assertThat(endpoint, notNullValue());

		server.enqueue(prepareResponse(ECHO_STRING).setHeader(CONTENT_TYPE_HEADER, MediaType.PLAIN_TEXT_UTF_8));
		Maybe<String> helloRS = endpoint.postFor(RESOURCE, ECHO_STRING, String.class);
		Assert.assertThat(helloRS.blockingGet(), is(ECHO_STRING));

	}

	/**
	 * Put wrong serializer into non-default configuration
	 *
	 * @throws RestEndpointIOException
	 */
	@Test(expected = SerializerException.class)
	public void testNoSerializer() throws RestEndpointIOException, ExecutionException, InterruptedException {
		RestEndpoint endpoint = RestEndpoints.create()
				.withBaseUrl(HTTP_TEST_URK + server.getPort())
				.withSerializer(new ByteArraySerializer())
				.build();
		Assert.assertThat(endpoint, notNullValue());

		server.enqueue(prepareResponse(ECHO_STRING));
		Maybe<String> helloRS = endpoint.postFor(RESOURCE, ECHO_STRING, String.class);
		Assert.assertThat(helloRS.blockingGet(), is(ECHO_STRING));
	}

	@Test
	public void testBuilderHappy() throws RestEndpointIOException, ExecutionException, InterruptedException {
		RestEndpoint endpoint = RestEndpoints.create()
				.withBaseUrl(HTTP_TEST_URK + server.getPort())
				.withSerializer(new StringSerializer())
				.build();
		Assert.assertThat(endpoint, notNullValue());

		server.enqueue(prepareResponse(ECHO_STRING));
		Maybe<String> helloRS = endpoint.postFor(RESOURCE, ECHO_STRING, String.class);
		Assert.assertThat(helloRS.blockingGet(), is(ECHO_STRING));
	}

	@Test
	public void testBuilderBasicAuth() throws RestEndpointIOException, InterruptedException, ExecutionException {
		RestEndpoint endpoint = RestEndpoints.create()
				.withBaseUrl(HTTP_TEST_URK + server.getPort())
				.withSerializer(new StringSerializer())
				.withBasicAuth("login", "password")
				.build();
		Assert.assertThat(endpoint, notNullValue());

		server.enqueue(prepareResponse(ECHO_STRING));
		endpoint.post(RESOURCE, ECHO_STRING, String.class).blockingGet();

		String basicAuthHeader = server.takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
		Assert.assertThat(basicAuthHeader, is("Basic " + Base64.encodeBase64String("login:password".getBytes())));
	}

	//TODO add test for SSL
}
