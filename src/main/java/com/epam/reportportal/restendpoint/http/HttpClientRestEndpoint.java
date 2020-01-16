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

import com.epam.reportportal.restendpoint.http.exception.RestEndpointIOException;
import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import com.epam.reportportal.restendpoint.serializer.Serializer;
import com.epam.reportportal.restendpoint.serializer.VoidSerializer;
import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.ByteSource;
import com.google.common.io.Closer;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * {@link RestEndpoint} implementation. Uses
 * Apache HTTP Components {@link HttpClient} as default
 * http client implementation
 *
 * @author Andrei Varabyeu
 */
public class HttpClientRestEndpoint implements RestEndpoint {

	private static final int DEFAULT_POOL_SIZE = 100;

	private static final long POOL_DRAIN_TIMEOUT = 1;
	private static final TimeUnit POOL_DRAIN_TIME_UNIT = TimeUnit.MINUTES;

	/**
	 * Serializer for converting HTTP messages
	 */
	private final List<Serializer> serializers;

	/**
	 * Base Endpoint URL
	 */
	private final String baseUrl;

	/**
	 * Error Handler for HttpResponses
	 */
	private final ErrorHandler errorHandler;

	/**
	 * HTTP Client
	 */
	private final HttpClient httpClient;

	/**
	 * Scheduler to be used with io operations
	 */
	private final Scheduler scheduler;

	private final ExecutorService executor;

	/**
	 * Default constructor.
	 *
	 * @param httpClient   Apache Http Client
	 * @param serializers  Serializer for converting HTTP messages. Shouldn't be null
	 * @param errorHandler Error handler for HTTP messages
	 */
	public HttpClientRestEndpoint(HttpClient httpClient, List<Serializer> serializers, ErrorHandler errorHandler) {
		this(httpClient, serializers, errorHandler, null);
	}

	/**
	 * Default constructor.
	 *
	 * @param httpClient   Apache Http Client
	 * @param serializers  Serializer for converting HTTP messages. Shouldn't be null
	 * @param errorHandler Error handler for HTTP messages
	 * @param baseUrl      REST WebService Base URL
	 */
	public HttpClientRestEndpoint(HttpClient httpClient, List<Serializer> serializers, ErrorHandler errorHandler, String baseUrl) {

		this(httpClient,
				serializers,
				errorHandler,
				baseUrl,
				Executors.newFixedThreadPool(DEFAULT_POOL_SIZE, new ThreadFactoryBuilder().setNameFormat("rp-io-%s").build())
		);
	}

	/**
	 * Default constructor.
	 *
	 * @param httpClient   Apache Http Client
	 * @param serializers  Serializer for converting HTTP messages. Shouldn't be null
	 * @param errorHandler Error handler for HTTP messages
	 * @param baseUrl      REST WebService Base URL
	 */
	public HttpClientRestEndpoint(HttpClient httpClient, List<Serializer> serializers, ErrorHandler errorHandler, String baseUrl,
			ExecutorService executorService) {
		executor = executorService;

		Preconditions.checkArgument(null != serializers && !serializers.isEmpty(), "There is no any serializer provided");
		//noinspection ConstantConditions
		this.serializers = ImmutableList.<Serializer>builder().addAll(serializers).add(new VoidSerializer()).build();

		if (!Strings.isNullOrEmpty(baseUrl)) {
			Preconditions.checkArgument(IOUtils.isValidUrl(baseUrl), "'%s' is not valid URL", baseUrl);
		}
		this.baseUrl = baseUrl;

		this.scheduler = Schedulers.from(executorService);

		this.errorHandler = errorHandler == null ? new DefaultErrorHandler() : errorHandler;
		this.httpClient = httpClient;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#post(java.lang .String,
	 * java.lang.Object, java.lang.Class)
	 */
	@Override
	public final <RQ, RS> Maybe<Response<RS>> post(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		HttpPost post = new HttpPost(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), toContentType(serializer.getMimeType()));
		post.setEntity(httpEntity);
		return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#postFor(java.lang .String,
	 * java.lang.Object, java.lang.Class)
	 */
	@Override
	public final <RQ, RS> Maybe<RS> postFor(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		return post(resource, rq, clazz).flatMap(new BodyTransformer<RS>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#post(java.lang.String,
	 * java.lang.Object, java.lang.reflect.Type)
	 */
	@Override
	public final <RQ, RS> Maybe<Response<RS>> post(String resource, RQ rq, Type type) throws RestEndpointIOException {
		HttpPost post = new HttpPost(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), toContentType(serializer.getMimeType()));
		post.setEntity(httpEntity);
		return executeInternal(post, new TypeConverterCallback<RS>(serializers, type));
	}

	@Override
	public final <RQ, RS> Maybe<RS> postFor(String resource, RQ rq, Type type) throws RestEndpointIOException {
		Maybe<Response<RS>> post = post(resource, rq, type);
		return post.flatMap(new BodyTransformer<RS>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#post(java.lang .String,
	 * MultiPartRequest, java.lang.Class)
	 */
	@Override
	public final <RS> Maybe<Response<RS>> post(String resource, MultiPartRequest request, Class<RS> clazz) throws RestEndpointIOException {
		HttpPost post = buildMultipartRequest(spliceUrl(resource), request);
		return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));
	}

	private HttpPost buildMultipartRequest(URI uri, MultiPartRequest request) throws RestEndpointIOException {
		HttpPost post = new HttpPost(uri);
		try {

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			for (MultiPartRequest.MultiPartSerialized<?> serializedPart : request.getSerializedRQs()) {
				Serializer serializer = getSupportedSerializer(serializedPart);
				builder.addPart(serializedPart.getPartName(),
						new StringBody(new String(serializer.serialize(serializedPart.getRequest()), Charsets.UTF_8),
								ContentType.parse(serializer.getMimeType().toString())
						)
				);
			}

			for (MultiPartRequest.MultiPartBinary partBinaty : request.getBinaryRQs()) {
				builder.addPart(partBinaty.getPartName(), new InputStreamBody(partBinaty.getData().openBufferedStream(),
						ContentType.parse(partBinaty.getContentType()),
						partBinaty.getFilename()
				));
			}
			post.setEntity(builder.build());

		} catch (Exception e) {
			throw new RestEndpointIOException("Unable to build post multipart request", e);
		}
		return post;
	}

	@Override
	public final <RS> Maybe<RS> postFor(String resource, MultiPartRequest request, Class<RS> clazz) throws RestEndpointIOException {
		return post(resource, request, clazz).flatMap(new BodyTransformer<RS>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#put(java.lang .String,
	 * java.lang.Object, java.lang.Class)
	 */
	@Override
	public final <RQ, RS> Maybe<Response<RS>> put(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		HttpPut put = new HttpPut(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), toContentType(serializer.getMimeType()));
		put.setEntity(httpEntity);
		return executeInternal(put, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public final <RQ, RS> Maybe<RS> putFor(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		return put(resource, rq, clazz).flatMap(new BodyTransformer<RS>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#put(java.lang.String,
	 * java.lang.Object, java.lang.reflect.Type)
	 */
	@Override
	public final <RQ, RS> Maybe<Response<RS>> put(String resource, RQ rq, Type type) throws RestEndpointIOException {
		HttpPut put = new HttpPut(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), toContentType(serializer.getMimeType()));
		put.setEntity(httpEntity);
		return executeInternal(put, new TypeConverterCallback<RS>(serializers, type));
	}

	@Override
	public final <RQ, RS> Maybe<RS> putFor(String resource, RQ rq, Type type) throws RestEndpointIOException {
		Maybe<Response<RS>> rs = put(resource, rq, type);
		return rs.flatMap(new BodyTransformer<RS>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#delete(java.
	 * lang.String, java.lang.Class)
	 */
	@Override
	public final <RS> Maybe<Response<RS>> delete(String resource, Class<RS> clazz) throws RestEndpointIOException {
		HttpDelete delete = new HttpDelete(spliceUrl(resource));
		return executeInternal(delete, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public final <RS> Maybe<RS> deleteFor(String resource, Class<RS> clazz) throws RestEndpointIOException {
		return delete(resource, clazz).flatMap(new BodyTransformer<RS>());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RestEndpoint#get(java.lang .String,
	 * java.lang.Class)
	 */
	@Override
	public final <RS> Maybe<Response<RS>> get(String resource, Class<RS> clazz) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource));
		return executeInternal(get, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public final <RS> Maybe<RS> getFor(String resource, Class<RS> clazz) throws RestEndpointIOException {
		return get(resource, clazz).flatMap(new BodyTransformer<RS>());
	}

	@Override
	public final <RS> Maybe<Response<RS>> get(String resource, Type type) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource));
		return executeInternal(get, new TypeConverterCallback<RS>(serializers, type));
	}

	@Override
	public final <RS> Maybe<RS> getFor(String resource, Type type) throws RestEndpointIOException {
		Maybe<Response<RS>> rs = get(resource, type);
		return rs.flatMap(new BodyTransformer<RS>());
	}

	@Override
	public final <RS> Maybe<Response<RS>> get(String resource, Map<String, String> parameters, Class<RS> clazz)
			throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource, parameters));
		return executeInternal(get, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public final <RS> Maybe<RS> getFor(String resource, Map<String, String> parameters, Class<RS> clazz) throws RestEndpointIOException {
		return get(resource, parameters, clazz).flatMap(new BodyTransformer<RS>());
	}

	@Override
	public final <RS> Maybe<Response<RS>> get(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource, parameters));
		return executeInternal(get, new TypeConverterCallback<RS>(serializers, type));
	}

	@Override
	public final <RS> Maybe<RS> getFor(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException {
		Maybe<Response<RS>> rs = get(resource, parameters, type);
		return rs.flatMap(new BodyTransformer<RS>());
	}

	/**
	 * Executes request command
	 *
	 * @param command REST request representation
	 * @return Future wrapper of REST response
	 * @throws RestEndpointIOException In case of error
	 * @see Maybe
	 */
	@Override
	public final <RQ, RS> Maybe<Response<RS>> executeRequest(RestCommand<RQ, RS> command) throws RestEndpointIOException {
		URI uri = spliceUrl(command.getUri());
		HttpUriRequest rq;
		Serializer serializer;
		switch (command.getHttpMethod()) {
			case GET:
				rq = new HttpGet(uri);
				break;
			case POST:
				if (command.isMultipart()) {
					MultiPartRequest rqData = (MultiPartRequest) command.getRequest();
					rq = buildMultipartRequest(uri, rqData);
				} else {
					serializer = getSupportedSerializer(command.getRequest());
					rq = new HttpPost(uri);
					((HttpPost) rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()),
							toContentType(serializer.getMimeType())
					));
				}
				break;
			case PUT:
				serializer = getSupportedSerializer(command.getRequest());
				rq = new HttpPut(uri);
				((HttpPut) rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()),
						toContentType(serializer.getMimeType())
				));
				break;
			case DELETE:
				rq = new HttpDelete(uri);
				break;
			case PATCH:
				serializer = getSupportedSerializer(command.getRequest());
				rq = new HttpPatch(uri);
				((HttpPatch) rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()),
						ContentType.create(serializer.getMimeType().toString())
				));
				break;
			default:
				throw new IllegalArgumentException("Method '" + command.getHttpMethod() + "' is unsupported");
		}

		return executeInternal(rq, new TypeConverterCallback<RS>(serializers, command.getResponseType()));
	}

	/**
	 * Splice base URL and URL of resource
	 *
	 * @param resource REST Resource Path
	 * @return Absolute URL to the REST Resource including server and port
	 * @throws RestEndpointIOException If URL is incorrect
	 */
	private URI spliceUrl(String resource) throws RestEndpointIOException {
		try {
			return Strings.isNullOrEmpty(baseUrl) ? new URI(resource) : new URI(baseUrl.concat(resource));
		} catch (URISyntaxException e) {
			throw new RestEndpointIOException("Unable to builder URL with base url '" + baseUrl + "' and resouce '" + resource + "'", e);
		}
	}

	/**
	 * Converts guava content type to Apache's one
	 *
	 * @param contentType Guava's content type
	 * @return Apache's content type
	 */
	private ContentType toContentType(MediaType contentType) {
		return ContentType.create(contentType.withoutParameters().toString(), contentType.charset().or(Charsets.UTF_8));
	}

	/**
	 * Splice base URL and URL of resource
	 *
	 * @param resource   REST Resource Path
	 * @param parameters Map of query parameters
	 * @return Absolute URL to the REST Resource including server and port
	 * @throws RestEndpointIOException In case of incorrect URL format
	 */
	final URI spliceUrl(String resource, Map<String, String> parameters) throws RestEndpointIOException {
		try {
			URIBuilder builder;
			if (!Strings.isNullOrEmpty(baseUrl)) {
				builder = new URIBuilder(baseUrl);
				builder.setPath(builder.getPath() + resource);
			} else {
				builder = new URIBuilder(resource);
			}
			for (Entry<String, String> parameter : parameters.entrySet()) {
				builder.addParameter(parameter.getKey(), parameter.getValue());
			}
			return builder.build();
		} catch (URISyntaxException e) {
			throw new RestEndpointIOException("Unable to builder URL with base url '" + baseUrl + "' and resouce '" + resource + "'", e);
		}
	}

	/**
	 * Finds supported serializer for this type of object
	 *
	 * @param o Object to be serialized
	 * @return Serializer
	 * @throws SerializerException if serializer not found
	 */
	private Serializer getSupportedSerializer(Object o) throws SerializerException {
		for (Serializer s : serializers) {
			if (s.canWrite(o)) {
				return s;
			}
		}
		throw new SerializerException("Unable to find serializer for object with type '" + o.getClass() + "'");
	}

	/**
	 * Executes {@link HttpUriRequest}
	 *
	 * @param rq       - Request
	 * @param callback - Callback to be applied on response
	 * @param <RS>     type of response
	 * @return - Serialized Response Body
	 */
	private <RS> Maybe<Response<RS>> executeInternal(final HttpUriRequest rq, final HttpEntityCallback<RS> callback) {
		if(executor.isShutdown()) {
			throw new IllegalStateException("Executor pool shut down");
		}

		final Closer closer = Closer.create();

		return Maybe.create(new MaybeOnSubscribe<Response<RS>>() {
			@Override
			public void subscribe(MaybeEmitter<Response<RS>> emitter) throws Exception {

				try {

					HttpResponse response = httpClient.execute(rq);

					LazyByteSource bodySupplier = new LazyByteSource(response.getEntity());
					closer.register(bodySupplier);

					/* convert headers to multimap */
					Header[] allHeaders = response.getAllHeaders();
					ImmutableMultimap.Builder<String, String> headersBuilder = ImmutableMultimap.builder();
					for (Header header : allHeaders) {
						headersBuilder.put(header.getName().toLowerCase(), null == header.getValue() ? "" : header.getValue());
					}

					/* convert entire response */
					Response<ByteSource> rs1 = new Response<ByteSource>(
							rq.getURI(),
							HttpMethod.valueOf(rq.getMethod()),
							response.getStatusLine().getStatusCode(),
							response.getStatusLine().getReasonPhrase(),
							headersBuilder.build(),
							bodySupplier
					);

					/* check whether there is error in the response */
					if (errorHandler.hasError(rs1)) {
						errorHandler.handle(rs1);
					}

					/* parse Content-Type header to be able to find appropriate serializer */
					MediaType contentType = null == response.getEntity().getContentType() ?
							MediaType.ANY_TYPE :
							MediaType.parse(response.getEntity().getContentType().getValue());

					/* build response with converted instance */
					if(!executor.isShutdown()) {
						emitter.onSuccess(new Response<RS>(
								rs1.getUri(),
								rs1.getHttpMethod(),
								rs1.getStatus(),
								rs1.getReason(),
								rs1.getHeaders(),
								callback.callback(contentType, bodySupplier.read())
						));
					}
				} catch (Throwable error) {
					if(!executor.isShutdown()) {
						emitter.onError(error);
					}
				} finally {
					closer.close();
				}
			}
		}).subscribeOn(scheduler);
	}

	@Override
	public void close() {
		executor.shutdown();
		try {
			executor.awaitTermination(POOL_DRAIN_TIMEOUT, POOL_DRAIN_TIME_UNIT);
		} catch (InterruptedException ignore) { // someone halted our execution
		}
		if (httpClient instanceof CloseableHttpClient) {
			IOUtils.closeQuietly((CloseableHttpClient) this.httpClient);
		}
	}

	private static abstract class HttpEntityCallback<RS> {

		final List<Serializer> serializers;

		/**
		 * Response callback
		 *
		 * @param serializers Serializers list
		 */
		HttpEntityCallback(List<Serializer> serializers) {
			this.serializers = serializers;
		}

		/**
		 * Performs callback on http entity
		 *
		 * @param contentType Response content type
		 * @param body        Response body
		 * @return Serialized RS body
		 * @throws IOException In case of IO error
		 */
		abstract public RS callback(MediaType contentType, byte[] body) throws IOException;
	}

	private static class TypeConverterCallback<RS> extends HttpEntityCallback<RS> {

		private final Type type;

		/**
		 * Callback based on Type
		 *
		 * @param serializers List of serializers
		 * @param type        Type of object
		 */
		TypeConverterCallback(List<Serializer> serializers, Type type) {
			super(serializers);
			this.type = type;
		}

		@Override
		public RS callback(MediaType contentType, byte[] body) throws IOException {
			return getSupported(contentType, type).deserialize(body, type);
		}

		/**
		 * Finds supported serializer
		 *
		 * @param contentType ContentType
		 * @param resultType  Result object Type
		 * @return Found Serializer
		 * @throws SerializerException If not serializer found
		 */
		Serializer getSupported(MediaType contentType, Type resultType) throws SerializerException {
			for (Serializer s : serializers) {
				if (s.canRead(contentType, resultType)) {
					return s;
				}
			}
			throw new SerializerException("Conversion media type '" + contentType + "' to type '" + resultType + "' is not supported");
		}

	}

	private static class ClassConverterCallback<RS> extends HttpEntityCallback<RS> {

		private final Class<RS> clazz;

		/**
		 * Callback based on Type
		 *
		 * @param serializers List of serializers
		 * @param clazz       Type of object
		 */
		ClassConverterCallback(List<Serializer> serializers, Class<RS> clazz) {
			super(serializers);
			this.clazz = clazz;
		}

		@Override
		public RS callback(MediaType contentType, byte[] body) throws IOException {
			return getSupported(contentType, clazz).deserialize(body, clazz);
		}

		/**
		 * Finds supported serializer
		 *
		 * @param contentType ContentType
		 * @param resultType  Result object Type
		 * @return Found Serializer
		 * @throws SerializerException If not serializer found
		 */
		private Serializer getSupported(MediaType contentType, Class<?> resultType) throws SerializerException {
			for (Serializer s : serializers) {
				if (s.canRead(contentType, resultType)) {
					return s;
				}
			}
			throw new SerializerException("Conversion media type '" + contentType + "' to type '" + resultType + "' is not supported");
		}

	}

	/**
	 * Transforms response object to Body
	 *
	 * @param <T> Type of body
	 */
	public static final class BodyTransformer<T> implements Function<Response<T>, Maybe<T>> {

		@Override
		public Maybe<T> apply(Response<T> input) {
			final T body = input.getBody();
			return (null == body ? Maybe.<T>empty() : Maybe.just(body));
		}
	}

	private static class LazyByteSource extends ByteSource implements Closeable {

		private final HttpEntity httpEntity;
		private final Supplier<ByteSource> supplier;

		private LazyByteSource(final HttpEntity httpEntity) {
			this.httpEntity = httpEntity;
			this.supplier = Suppliers.memoize(new Supplier<ByteSource>() {
				@Override
				public ByteSource get() {
					return ByteSource.wrap(readEntity(httpEntity));
				}
			});
		}

		@Override
		public InputStream openStream() throws IOException {
			return supplier.get().openStream();
		}

		/**
		 * Parses byte from entity
		 *
		 * @param entity HTTP Entity
		 * @return Body as byte array
		 * @throws RestEndpointIOException In case of request error
		 */
		private byte[] readEntity(HttpEntity entity) {
			try {
				return EntityUtils.toByteArray(entity);
			} catch (IOException e) {
				throw new RestEndpointIOException("Unable to read body from error", e);
			} finally {
				EntityUtils.consumeQuietly(entity);
			}
		}

		@Override
		public void close() throws IOException {
			EntityUtils.consumeQuietly(httpEntity);
		}
	}
}
