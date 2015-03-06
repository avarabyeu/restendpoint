package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.net.MediaType;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Calendar;

/**
 * Tests for {@link com.github.avarabyeu.restendpoint.serializer.TextSerializer}
 * @author avarabyeu
 */
public class TextSerializerTest {

    public static final String TEST_STRING = "test";
    private TextSerializer serializer = new TextSerializer();

    @Test
    public void testSerialize() throws SerializerException {
        byte[] result = serializer.serialize(TEST_STRING);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_STRING.getBytes()), "Incorrect serialization result");
    }

    @Test
    public void testDeserialize() throws SerializerException {
        String result = serializer.deserialize(TEST_STRING.getBytes(), String.class);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_STRING), "Incorrect deserialization result");
    }

    @Test
    public void testContentType() {
        SmartAssert.assertSoft(serializer.canRead(MediaType.OCTET_STREAM, String.class),
                CoreMatchers.is(false), "Wrong content type handling - octeat/stream");

        SmartAssert.assertSoft(serializer.canRead(MediaType.ANY_TEXT_TYPE, String.class),
                CoreMatchers.is(true), "Wrong content type handling - any text type");

        SmartAssert.assertSoft(serializer.canRead(MediaType.ANY_TEXT_TYPE, byte[].class),
                CoreMatchers.is(true), "Wrong result type handling - cannot read byte array");

        SmartAssert.assertSoft(serializer.canWrite(new byte[]{}),
                CoreMatchers.is(false), "Wrong content type handling. Can write byte array");

        SmartAssert.assertSoft(serializer.canWrite("some string"),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write string");

        SmartAssert.assertSoft(serializer.canWrite(new Long(100)),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write Long");

        SmartAssert.assertSoft(serializer.canWrite(Calendar.getInstance().getTime()),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write Date");

        SmartAssert.validateSoftAsserts();
    }
}
