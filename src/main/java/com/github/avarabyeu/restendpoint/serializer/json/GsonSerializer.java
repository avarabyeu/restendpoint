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

package com.github.avarabyeu.restendpoint.serializer.json;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * JSON serializer using GSON library
 *
 * @author Andrei Varabyeu
 * @see <a href="https://code.google.com/p/google-gson/">GSON</a>
 */
public class GsonSerializer extends AbstractJsonSerializer {

    private final Gson gson;

    /**
     * Creates serializer with provided GSON
     *
     * @param gson Gson object
     */
    public GsonSerializer(Gson gson) {
        this.gson = gson;
    }

    /**
     * Creates serializer with default GSON
     */
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
        try (BufferedReader is = ByteSource.wrap(content).asCharSource(Charsets.UTF_8).openBufferedStream()) {
            return gson.fromJson(is, clazz);
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

}
