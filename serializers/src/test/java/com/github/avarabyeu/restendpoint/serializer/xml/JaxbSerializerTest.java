package com.github.avarabyeu.restendpoint.serializer.xml;

import com.github.avarabyeu.restendpoint.serializer.DemoBean;
import com.github.avarabyeu.restendpoint.serializer.SerializerException;
import com.google.common.net.MediaType;
import com.smarttested.qa.smartassert.SmartAssert;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Andrei Varabyeu
 */
public class JaxbSerializerTest {

    private static final String TEST_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><demoBean><someField>someValue</someField></demoBean>";
    private static final DemoBean TEST_BEAN = new DemoBean("someValue");

    private static JaxbSerializer serializer;

    @BeforeClass
    public static void prepare() throws SerializerException {
        serializer = new JaxbSerializer(DemoBean.class);
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
        DemoBean result = serializer.deserialize(TEST_STRING.getBytes(), DemoBean.class);
        SmartAssert.assertHard(
                result,
                CoreMatchers
                        .is(TEST_BEAN), "Incorrect deserialization result");
    }

    @Test(expected = SerializerException.class)
    public void testDeserializeNotXml() throws SerializerException {
        serializer.deserialize("this is not xml".getBytes(), DemoBean.class);
    }

    @Test
    public void testContentType() {
        SmartAssert.assertSoft(serializer.canRead(MediaType.OCTET_STREAM, Object.class),
                CoreMatchers.is(false), "Wrong content type handling - octeat/stream");

        SmartAssert.assertSoft(serializer.canRead(MediaType.APPLICATION_XML_UTF_8, Object.class),
                CoreMatchers.is(true), "Wrong content type handling - cannot read application/xml");

        SmartAssert.assertSoft(serializer.canWrite(new DemoBean()),
                CoreMatchers.is(true), "Wrong content type handling. Cannot write test object");

        SmartAssert.validateSoftAsserts();
    }

    @Test(expected = SerializerException.class)
    public void testWrongContextPath() throws SerializerException {
        new JaxbSerializer(this.getClass().getPackage().getName());
    }
}
