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

package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.serializer.Serializer;
import com.epam.reportportal.restendpoint.serializer.StringSerializer;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.QueueDispatcher;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

/**
 * Google Guice Module for testing purposes
 *
 * @author Andrei Varabyeu
 */
public class GuiceTestModule implements Module {

	public static final Key<ErrorHandler> ERROR_HANDLER_KEY = new Key<ErrorHandler>() {
	};

	@Override
	public void configure(Binder binder) {

		/** Error Handler binding */
		binder.bind(ERROR_HANDLER_KEY).to(DefaultErrorHandler.class).in(Scopes.SINGLETON);

		binder.bind(MockWebServer.class).in(Scopes.NO_SCOPE);

	}

	/**
	 * Default {@link Serializer} binding
	 *
	 * @return Serializer
	 */
	@Provides
	public Serializer provideSeriazer() {
		return new StringSerializer();
	}

	@Provides
	@Named("slow")
	public MockWebServer provideSlowWsMock() {
		MockWebServer mockWebServer = new MockWebServer();
		mockWebServer.setDispatcher(new QueueDispatcher() {
			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				Uninterruptibles.sleepUninterruptibly(3L, TimeUnit.SECONDS);
				return super.dispatch(request);
			}
		});
		return mockWebServer;
	}

	private static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			System.out.println("Used Port: " + socket.getLocalPort());
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException("Unable to find free port", e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

}
