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

package com.github.avarabyeu.restendpoint.http;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.async.Wills;
import com.github.avarabyeu.restendpoint.http.exception.RestEndpointIOException;
import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.github.avarabyeu.restendpoint.serializer.Serializer;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SettableFuture;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * {@link RestEndpoint} implementation. Uses
 * Apache HTTP Components {@link org.apache.http.client.HttpClient} as default
 * http client implementation
 *
 * @author Andrei Varabyeu
 */
public class HttpClientRestEndpoint implements RestEndpoint, Closeable {

    /**
     * Serializer for converting HTTP messages
     */
    private List<Serializer> serializers;

    /**
     * Base Endpoint URL
     */
    private String baseUrl;

    /**
     * Error Handler for HttpResponses
     */
    private ErrorHandler<HttpResponse> errorHandler;

    /**
     * HTTP Client
     */
    private CloseableHttpAsyncClient httpClient;

    /**
     * Default constructor.
     *
     * @param httpClient   Apache Async Http Client
     * @param serializers  Serializer for converting HTTP messages. Shouldn't be null
     * @param errorHandler Error handler for HTTP messages
     * @param baseUrl      REST WebService Base URL
     */
    public HttpClientRestEndpoint(CloseableHttpAsyncClient httpClient, List<Serializer> serializers, ErrorHandler<HttpResponse> errorHandler,
                                  String baseUrl) {

        Preconditions.checkArgument(null != serializers && !serializers.isEmpty(), "There is no any serializer provided");
        this.serializers = serializers;

        Preconditions.checkArgument(IOUtils.isValidUrl(baseUrl), "'%s' is not valid URL", baseUrl);
        this.baseUrl = baseUrl;

        this.errorHandler = errorHandler == null ? new DefaultErrorHandler() : errorHandler;
        this.httpClient = httpClient;
        if (!httpClient.isRunning()) {
            httpClient.start();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#post(java.lang .String,
     * java.lang.Object, java.lang.Class)
     */
    @Override
    public <RQ, RS> Will<RS> post(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
        HttpPost post = new HttpPost(spliceUrl(resource));
        Serializer serializer = getSupportedSerializer(rq);
        ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
        post.setEntity(httpEntity);
        return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));

    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#post(java.lang.String,
     * java.lang.Object, java.lang.reflect.Type)
     */
    @Override
    public <RQ, RS> Will<RS> post(String resource, RQ rq, Type type) throws RestEndpointIOException {
        HttpPost post = new HttpPost(spliceUrl(resource));
        Serializer serializer = getSupportedSerializer(rq);
        ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
        post.setEntity(httpEntity);
        return executeInternal(post, new TypeConverterCallback<RS>(serializers, type));

    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#post(java.lang .String,
     * MultiPartRequest, java.lang.Class)
     */
    @Override
    public <RS> Will<RS> post(String resource, MultiPartRequest request, Class<RS> clazz) throws RestEndpointIOException {
        HttpPost post = new HttpPost(spliceUrl(resource));

        try {

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (MultiPartRequest.MultiPartSerialized<?> serializedPart : request.getSerializedRQs()) {
                Serializer serializer = getSupportedSerializer(serializedPart);
                builder.addPart(serializedPart.getPartName(), new StringBody(new String(serializer.serialize(serializedPart.getRequest())),
                        ContentType.parse(serializer.getMimeType())));
            }

            for (MultiPartRequest.MultiPartBinary partBinaty : request.getBinaryRQs()) {
                builder.addPart(
                        partBinaty.getPartName(),
                        new ByteArrayBody(partBinaty.getData().read(), ContentType.parse(partBinaty.getContentType()), partBinaty
                                .getFilename())
                );
            }

            /* Here is some dirty hack to avoid problem with MultipartEntity and asynchronous http client
             *  Details can be found here: http://comments.gmane.org/gmane.comp.apache.httpclient.user/2426
             *
             *  The main idea is to replace MultipartEntity with NByteArrayEntity once first doesn't support #getContent method
             *  which is required for async client implementation. So, we are copying response body as byte array to NByteArrayEntity to
             *  leave it unmodified.
             *
             *  Alse we need to add boundary value to content type header. Details are here: http://en.wikipedia.org/wiki/Delimiter#Content_boundary
             *  MultipartEntity generates correct header by yourself, but we need to put it manually once we replaced entity type to NByteArrayEntity
             */
            String boundary = "-------------" + UUID.randomUUID().toString();
            builder.setBoundary(boundary);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            builder.build().writeTo(baos);

            post.setEntity(new NByteArrayEntity(baos.toByteArray(), ContentType.MULTIPART_FORM_DATA));
            post.setHeader("Content-Type", "multipart/form-data;boundary=" + boundary);

        } catch (Exception e) {
            throw new RestEndpointIOException("Unable to build post multipart request", e);
        }
        return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));
    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#put(java.lang .String,
     * java.lang.Object, java.lang.Class)
     */
    @Override
    public <RQ, RS> Will<RS> put(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
        HttpPut put = new HttpPut(spliceUrl(resource));
        Serializer serializer = getSupportedSerializer(rq);
        ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
        put.setEntity(httpEntity);
        return executeInternal(put, new ClassConverterCallback<RS>(serializers, clazz));
    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#put(java.lang.String,
     * java.lang.Object, java.lang.reflect.Type)
     */
    @Override
    public <RQ, RS> Will<RS> put(String resource, RQ rq, Type type) throws RestEndpointIOException {
        HttpPut put = new HttpPut(spliceUrl(resource));
        Serializer serializer = getSupportedSerializer(rq);
        ByteArrayEntity httpEntity = new ByteArrayEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
        put.setEntity(httpEntity);
        return executeInternal(put, new TypeConverterCallback<RS>(serializers, type));
    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#delete(java.
     * lang.String, java.lang.Class)
     */
    @Override
    public <RS> Will<RS> delete(String resource, Class<RS> clazz) throws RestEndpointIOException {
        HttpDelete delete = new HttpDelete(spliceUrl(resource));
        return executeInternal(delete, new ClassConverterCallback<RS>(serializers, clazz));
    }

    /*
     * (non-Javadoc)
     *
     * @see RestEndpoint#get(java.lang .String,
     * java.lang.Class)
     */
    @Override
    public <RS> Will<RS> get(String resource, Class<RS> clazz) throws RestEndpointIOException {
        HttpGet get = new HttpGet(spliceUrl(resource));
        return executeInternal(get, new ClassConverterCallback<RS>(serializers, clazz));
    }

    @Override
    public <RS> Will<RS> get(String resource, Type type) throws RestEndpointIOException {
        HttpGet get = new HttpGet(spliceUrl(resource));
        return executeInternal(get, new TypeConverterCallback<RS>(serializers, type));
    }

    @Override
    public <RS> Will<RS> get(String resource, Map<String, String> parameters, Class<RS> clazz) throws RestEndpointIOException {
        HttpGet get = new HttpGet(spliceUrl(resource, parameters));
        return executeInternal(get, new ClassConverterCallback<RS>(serializers, clazz));
    }

    @Override
    public <RS> Will<RS> get(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException {
        HttpGet get = new HttpGet(spliceUrl(resource, parameters));
        return executeInternal(get, new TypeConverterCallback<RS>(serializers, type));
    }

    /**
     * Executes request command
     *
     * @param command REST request representation
     * @return Future wrapper of REST response
     * @throws RestEndpointIOException
     * @see {@link com.github.avarabyeu.restendpoint.async.Will}
     */
    @Override
    public <RQ, RS> Will<RS> executeRequest(RestCommand<RQ, RS> command) throws RestEndpointIOException {
        URI uri = spliceUrl(command.getUri());
        HttpUriRequest rq;
        Serializer serializer;
        switch (command.getHttpMethod()) {
            case GET:
                rq = new HttpGet(uri);
                break;
            case POST:
                serializer = getSupportedSerializer(command.getRequest());
                rq = new HttpPost(uri);
                ((HttpPost) rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()), ContentType.create(
                        serializer.getMimeType())));
                break;
            case PUT:
                serializer = getSupportedSerializer(command.getRequest());
                rq = new HttpPut(uri);
                ((HttpPut) rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()), ContentType.create(
                        serializer.getMimeType())));
                break;
            case DELETE:
                rq = new HttpDelete(uri);
                break;
            case PATCH:
                serializer = getSupportedSerializer(command.getRequest());
                rq = new HttpPatch(uri);
                ((HttpPatch) rq).setEntity(new ByteArrayEntity(serializer.serialize(command.getRequest()), ContentType.create(
                        serializer.getMimeType())));
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
     * @throws RestEndpointIOException
     */
    private URI spliceUrl(String resource) throws RestEndpointIOException {
        try {
            return new URI(baseUrl.concat(resource));
        } catch (URISyntaxException e) {
            throw new RestEndpointIOException("Unable to builder URL with base url '" + baseUrl + "' and resouce '" + resource + "'", e);
        }
    }

    /**
     * Splice base URL and URL of resource
     *
     * @param resource   REST Resource Path
     * @param parameters Map of query parameters
     * @return Absolute URL to the REST Resource including server and port
     * @throws RestEndpointIOException
     */
    private URI spliceUrl(String resource, Map<String, String> parameters) throws RestEndpointIOException {
        try {
            URIBuilder builder = new URIBuilder(baseUrl).setPath(resource);
            for (Entry<String, String> parameter : parameters.entrySet()) {
                builder.addParameter(parameter.getKey(), parameter.getValue());
            }
            return builder.build();
        } catch (URISyntaxException e) {
            throw new RestEndpointIOException("Unable to builder URL with base url '" + baseUrl + "' and resouce '" + resource + "'", e);
        }
    }

    private Serializer getSupportedSerializer(Object o) throws SerializerException {
        for (Serializer s : serializers) {
            if (s.canWrite(o)) {
                return s;
            }
        }
        throw new SerializerException("Unable to find serializer for object with type '" + o.getClass() + "'");
    }

    /**
     * Executes {@link org.apache.http.client.methods.HttpUriRequest}
     *
     * @param rq       - Request
     * @param callback - Callback to be applied on response
     * @return - Serialized Response Body
     * @throws RestEndpointIOException
     */
    private <RS> Will<RS> executeInternal(HttpUriRequest rq, final HttpEntityCallback<RS> callback) throws RestEndpointIOException {

        final SettableFuture<RS> future = SettableFuture.create();
        httpClient.execute(rq, new FutureCallback<org.apache.http.HttpResponse>() {

                    @Override
                    public void completed(final org.apache.http.HttpResponse response) {
                        try {
                            if (errorHandler.hasError(response)) {
                                errorHandler.handle(response);
                            }
                            HttpEntity entity = response.getEntity();
                            future.set(callback.callback(entity));
                        } catch (SerializerException e) {
                            future.setException(e);
                        } catch (IOException e) {
                            future.setException(new RestEndpointIOException("Unable to execute request", e));
                        } catch (Exception e) {
                            future.setException(e);
                        }

                    }

                    @Override
                    public void failed(final Exception ex) {
                        future.setException(new RestEndpointIOException("Unable to execute request", ex));
                    }

                    @Override
                    public void cancelled() {
                        final TimeoutException timeoutException = new TimeoutException();
                        future.setException(timeoutException);
                    }

                }
        );
        return Wills.forListenableFuture(future);


    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    private static abstract class HttpEntityCallback<RS> {

        protected List<Serializer> serializers;

        public HttpEntityCallback(List<Serializer> serializers) {
            this.serializers = serializers;
        }

        protected Serializer getSupported(String contentType) throws SerializerException {
            for (Serializer s : serializers) {
                if (s.canRead(contentType)) {
                    return s;
                }
            }
            throw new SerializerException("Unsupported media type '" + contentType);
        }

        abstract public RS callback(HttpEntity entity) throws IOException;
    }

    private static class TypeConverterCallback<RS> extends HttpEntityCallback<RS> {

        private Type type;

        public TypeConverterCallback(List<Serializer> serializers, Type type) {
            super(serializers);
            this.type = type;
        }

        @Override
        public RS callback(HttpEntity entity) throws IOException {
            return getSupported(entity.getContentType().getValue()).deserialize(EntityUtils.toByteArray(entity), type);
        }

    }

    private static class ClassConverterCallback<RS> extends HttpEntityCallback<RS> {

        private Class<RS> clazz;

        public ClassConverterCallback(List<Serializer> serializers, Class<RS> clazz) {
            super(serializers);
            this.clazz = clazz;
        }

        @Override
        public RS callback(HttpEntity entity) throws IOException {
            return getSupported(ContentType.get(entity).getMimeType()).deserialize(EntityUtils.toByteArray(entity), clazz);
        }

    }
}
