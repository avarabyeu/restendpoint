package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * Created by andrey.vorobyov on 20/06/14.
 */
public class GsonSerializer implements Serializer {

    private Gson gson;

    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }

    public GsonSerializer() {
        this(new Gson());
    }


    @Override
    public <T> byte[] serialize(T t) throws SerializerException {
        try {
            return gson.toJson(t).getBytes(Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new SerializerException("UTF-8 is not supported", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        try {
            return gson.fromJson(ByteSource.wrap(content).asCharSource(Charsets.UTF_8).openBufferedStream(), clazz);
        } catch (IOException e) {
            throw new SerializerException("Unable to serialize content", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] content, Type type) throws SerializerException {
        try {
            return (T) gson.getAdapter(com.google.gson.reflect.TypeToken.<T>get(type))
                    .fromJson(ByteSource.wrap(content).asCharSource(Charsets.UTF_8).openBufferedStream());
        } catch (IOException e) {
            throw new SerializerException("Unable to serialize content", e);
        }
    }

    @Override
    public String getMimeType() {
        return MediaType.JSON_UTF_8.toString();
    }

    @Override
    public boolean canRead(String mimeType) {
        return MediaType.JSON_UTF_8.withoutParameters().is(MediaType.parse(mimeType).withoutParameters());
    }

    /**
     * GSON can try to serialize and object so just leave TRUE here
     *
     * @param o - Object to be serialized
     * @return
     */
    @Override
    public boolean canWrite(Object o) {
        return true;
    }
}
