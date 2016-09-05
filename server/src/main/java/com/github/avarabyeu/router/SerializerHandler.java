package com.github.avarabyeu.router;

import com.github.avarabyeu.restendpoint.serializer.Serializer;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by avarabyeu on 12/18/15.
 */
class SerializerHandler<RQ> implements RequestHandler {

    private final TypedBodySupplier<RQ> delegate;
    private final Optional<Class<RQ>> requestBodyType;
    private final Serializer serializer;

    public SerializerHandler(Serializer serializer, Class<RQ> rqType, TypedBodySupplier<RQ> delegate) {
        this(serializer, Optional.of(rqType), delegate);
    }

    public SerializerHandler(Serializer serializer, BodySupplier delegate) {
        //noinspection unchecked
        this(serializer, Optional.empty(), delegate);
    }

    private SerializerHandler(Serializer serializer, Optional<Class<RQ>> rqType, TypedBodySupplier<RQ> delegate) {
        this.serializer = Preconditions.checkNotNull(serializer, "Serializer shouldn't be null");
        this.delegate = delegate;
        this.requestBodyType = rqType;
    }

    @Override
    public final void handle(Request request, Response response) throws IOException {
        if (requestBodyType.isPresent() && !request.getMethod().hasBody()) {
            //warn?
        }

        RQ requestBody = null;
        if (requestBodyType.isPresent()) {

            Optional<MediaType> requestContentType = request.getContentType();
            if (serializer.canRead(requestContentType.orElse(MediaType.ANY_TYPE), requestBodyType.get())) {
                byte[] bodyBytes = request.getBodyAs(ByteStreams::toByteArray);
                requestBody = serializer.deserialize(bodyBytes, requestBodyType.get());
            } else {
                throw new IllegalArgumentException(
                        String.format("Cannot deserialize MimeType %s", requestContentType));
            }
        }

        Object responseBody = delegate.handle(request, response, requestBody);
        if (serializer.canWrite(responseBody)) {
            response.contentType(serializer.getMimeType());
            response.content(serializer.serialize(responseBody));
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot serialize object %s to %s", responseBody, serializer.getMimeType()));
        }
    }

    @Override
    public boolean supports(Route route) {
        /* if rq body type is provided than route should support body-containing method */
        return !requestBodyType.isPresent() || !route.getMethod().isPresent() || route.getMethod().get().hasBody();

    }

    @Override
    public String toString() {
        return "SerializerHandler{" +
                "delegate=" + delegate +
                ", requestBodyType=" + requestBodyType +
                ", serializer=" + serializer +
                '}';
    }

    @FunctionalInterface
    public interface TypedBodySupplier<RQ> {
        Object handle(Request rq, Response rs, RQ body) throws IOException;
    }

    @FunctionalInterface
    public interface BodySupplier extends TypedBodySupplier {
        Object handle(Request rq, Response rs);

        default Object handle(Request rq, Response rs, Object body) {
            return handle(rq, rs);
        }
    }
}
