package com.github.avarabyeu.restendpoint.serializer.xml;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.github.avarabyeu.restendpoint.serializer.TestBean;
import com.github.avarabyeu.restendpoint.serializer.xml.JaxbSerializer;
import com.google.common.net.MediaType;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by andrey.vorobyov on 9/25/14.
 */
public class JaxbSerializerTest {

    private static final String TEST_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testBean><someField>someValue</someField></testBean>";
    private static final TestBean TEST_BEAN = new TestBean("someValue");

    private static JaxbSerializer serializer;

    @BeforeClass
    public static void prepare() throws SerializerException {
        serializer = new JaxbSerializer(TestBean.class);
    }

    @Test
    public void testSerialize() throws SerializerException {
        byte[] result = serializer.serialize(TEST_BEAN);
        SmartAssert.assertHard(
                new String(result),
                CoreMatchers
                        .is(TEST_STRING), "Incorrect serialization result");
    }

    @Test(expected = SerializerException.class)
    public void testSerializeWrongObject() throws SerializerException {
        serializer.serialize("this is not a JAXB bean");
    }

    @Test
    public void testDeserialize() throws SerializerException {
        TestBean result = serializer.deserialize(TEST_STRING.getBytes(), TestBean.class);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_BEAN), "Incorrect deserialization result");
    }

    @Test(expected = SerializerException.class)
    public void testDeserializeNotXml() throws SerializerException {
        serializer.deserialize("this is not xml".getBytes(), TestBean.class);
    }

    @Test
    public void testContentType() {
        SmartAssert.assertSoft(serializer.canRead(MediaType.OCTET_STREAM),
                CoreMatchers.is(false), "Wrong content type handling - octeat/stream");

        SmartAssert.assertSoft(serializer.canRead(MediaType.APPLICATION_XML_UTF_8),
                CoreMatchers.is(true), "Wrong content type handling - cannot read application/xml");

        SmartAssert.assertSoft(serializer.canWrite(new TestBean()),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write test object");

        SmartAssert.validateSoftAsserts();
    }

    @Test(expected = SerializerException.class)
    public void testWrongContextPath() throws SerializerException {
        new JaxbSerializer(this.getClass().getPackage().getName());
    }
}
