package com.github.avarabyeu.restendpoint.serializer;

import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.google.common.base.Charsets;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Text Serializer based on Apache Conversion Utils
 *
 * @author Andrei Varabyeu
 * @see
 */
public class TextSerializer implements Serializer {

    private final ConvertUtilsBean converter;

    /**
     * Creates TextSerializer with default configuration like throwing exceptions, using NULL as default value, etc
     */
    public TextSerializer() {
        this(true, true, 0);
    }


    /**
     * Creates TextSerializer with custom configuration like throwing exceptions, using NULL as default value, etc
     *
     * @param throwException   <code>true</code> if the converters should
     *                         throw an exception when a conversion error occurs, otherwise
     *                         <code>false</code> if a default value should be used.
     * @param defaultNull      <code>true</code>if the <i>standard</i> converters
     *                         (see {@link ConvertUtilsBean#registerStandard(boolean, boolean)})
     *                         should use a default value of <code>null</code>, otherwise <code>false</code>.
     *                         N.B. This values is ignored if <code>throwException</code> is <code>true</code>
     * @param defaultArraySize The size of the default array value for array converters
     *                         (N.B. This values is ignored if <code>throwException</code> is <code>true</code>).
     *                         Specifying a value less than zero causes a <code>null</code> value to be used for
     *                         the default.
     * @see {@link org.apache.commons.beanutils.ConvertUtilsBean#register(boolean, boolean, int)}
     */
    public TextSerializer(boolean throwException, boolean defaultNull, int defaultArraySize) {
        this.converter = new ConvertUtilsBean();

        /* throw exception, default value is null, default array size is 0 */
        this.converter.register(throwException, defaultNull, defaultArraySize);

        /* DO NOT USE as byte array serializer since it produce wrong content type */
        this.converter.deregister(byte[].class);
        this.converter.deregister(Byte[].class);
    }


    /**
     * Creates default TextSerializer and registers additional type converters
     *
     * @param typeConverters Map of type converters for serializer
     */
    public TextSerializer(Map<Converter, Class<?>> typeConverters) {
        this();

        for (Map.Entry<Converter, Class<?>> typeConverter : typeConverters.entrySet()) {
            this.converter.register(typeConverter.getKey(), typeConverter.getValue());
        }
    }


    @Override
    public <T> byte[] serialize(T t) throws SerializerException {
        try {
            return converter.lookup(String.class).convert(String.class, t).getBytes(Charsets.UTF_8);
        } catch (ConversionException e) {
            throw new SerializerException("Cannot convert content '" + t + "' to string type", e.getCause());
        }
    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        String stringContent = new String(content, Charsets.UTF_8);
        try {
            return converter.lookup(clazz).convert(clazz, stringContent);
        } catch (ConversionException e) {
            throw new SerializerException("Cannot convert content '" + stringContent + "' to type [" + clazz + "]", e.getCause());
        }

    }

    @Override
    public <T> T deserialize(byte[] content, Type type) throws SerializerException {
        //noinspection unchecked
        return deserialize(content, (Class<T>) TypeToken.of(type).getRawType());
    }

    @Override
    public String getMimeType() {
        return MediaType.PLAIN_TEXT_UTF_8.toString();
    }

    @Override
    public boolean canRead(MediaType mimeType, Class<?> resultType) {
        return mimeType.withoutParameters().is(MediaType.ANY_TEXT_TYPE) && null != converter.lookup(resultType, String.class);
    }

    @Override
    public boolean canRead(MediaType mimeType, Type resultType) {
        return canRead(mimeType, TypeToken.of(resultType).getRawType());
    }

    @Override
    public boolean canWrite(Object o) {
        return null != converter.lookup(o.getClass());
    }
}
