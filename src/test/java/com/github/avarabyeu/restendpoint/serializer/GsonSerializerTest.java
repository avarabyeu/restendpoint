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
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * @author Andrey Vorobyov
 */
public class GsonSerializerTest {

    private static final String TEST_STRING = "{ \"someField\" : \"someValue\" }";
    private static final TestBean TEST_BEAN = new TestBean("someValue");

    private GsonSerializer serializer = new GsonSerializer();

    @Test
    public void testSerialize() throws SerializerException {
        byte[] result = serializer.serialize(TEST_BEAN);
        SmartAssert.verifyHard(
                result,
                CoreMatchers
                        .is(TEST_STRING.getBytes()), "Incorrect serialization result");
    }

    @Test
    public void testDeserialize() throws SerializerException {
        TestBean result = serializer.deserialize(TEST_STRING.getBytes(), TestBean.class);
        SmartAssert.verifyHard(
                result,
                CoreMatchers
                        .is(TEST_BEAN), "Incorrect deserialization result");
    }

    @Test
    public void testContentType() {
        SmartAssert.assertSoft(serializer.canRead(MediaType.OCTET_STREAM.toString()),
                CoreMatchers.is(false), "Wrong content type handling - octeat/stream");

        SmartAssert.assertSoft(serializer.canRead(MediaType.JSON_UTF_8.toString()),
                CoreMatchers.is(true), "Wrong content type handling - cannot read application/json");

        SmartAssert.assertSoft(serializer.canWrite(TestBean.class),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write byte array");

        SmartAssert.validateSoftAsserts();
    }
}
