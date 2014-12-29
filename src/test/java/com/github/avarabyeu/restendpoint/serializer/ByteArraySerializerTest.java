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

package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * @author Andrei Varabyeu
 */
public class ByteArraySerializerTest {

    private static final String TEST_STRING = "test";
    private ByteArraySerializer serializer = new ByteArraySerializer();

    @Test
    public void testSerialize() throws SerializerException {
        byte[] result = serializer.serialize(TEST_STRING.getBytes());
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_STRING.getBytes()), "Incorrect serialization result");
    }

    @Test
    public void testDeserialize() throws SerializerException {
        byte[] result = serializer.deserialize(TEST_STRING.getBytes(), byte[].class);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_STRING.getBytes()), "Incorrect deserialization result");
    }

    @Test
    public void testDeserializeByType() throws SerializerException {
        byte[] result = serializer.deserialize(TEST_STRING.getBytes(), TypeToken.of(byte[].class).getType());
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_STRING.getBytes()), "Incorrect deserialization result");
    }

    @Test
    public void testContentType() {
        SmartAssert.assertSoft(serializer.canRead(MediaType.OCTET_STREAM),
                CoreMatchers.is(true), "Wrong content type handling - octeat/stream");

        SmartAssert.assertSoft(serializer.canRead(MediaType.ANY_TEXT_TYPE),
                CoreMatchers.is(true), "Wrong content type handling");

        SmartAssert.assertSoft(serializer.canWrite(new byte[]{}),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write byte array");

        SmartAssert.assertSoft(serializer.canWrite("some string"),
                CoreMatchers.is(false), "Wrong content type handling. Can write string");

        SmartAssert.validateSoftAsserts();
    }

}
