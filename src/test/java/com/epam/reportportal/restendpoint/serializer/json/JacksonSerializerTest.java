package com.epam.reportportal.restendpoint.serializer.json;

import com.epam.reportportal.restendpoint.http.exception.SerializerException;
import com.epam.reportportal.restendpoint.serializer.DemoBean;
import com.google.common.net.MediaType;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * @author Andrei Varabyeu
 */
//TODO refactor to avoid duplicating with GsonSerializerTest
public class JacksonSerializerTest {

    private static final String TEST_STRING = "{\"someField\":\"someValue\"}";
    private static final DemoBean TEST_BEAN = new DemoBean("someValue");

    private final JacksonSerializer serializer = new JacksonSerializer();

    @Test
    public void testSerialize() throws SerializerException {
        byte[] result = serializer.serialize(TEST_BEAN);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_STRING.getBytes()), "Incorrect serialization result");
    }

    @Test
    public void testDeserialize() throws SerializerException {
        DemoBean result = serializer.deserialize(TEST_STRING.getBytes(), DemoBean.class);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_BEAN), "Incorrect deserialization result");
    }

    @Test
    public void testContentType() {
        SmartAssert.assertSoft(serializer.canRead(MediaType.OCTET_STREAM, byte[].class),
                CoreMatchers.is(false), "Wrong content type handling - octeat/stream");

        SmartAssert.assertSoft(serializer.canRead(MediaType.JSON_UTF_8, byte[].class),
                CoreMatchers.is(true), "Wrong content type handling - cannot read application/json");

        SmartAssert.assertSoft(serializer.canWrite(new DemoBean()),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write byte array");

        SmartAssert.validateSoftAsserts();
    }
}
