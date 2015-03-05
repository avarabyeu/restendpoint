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

package com.github.avarabyeu.restendpoint.serializer.xml;

import com.github.avarabyeu.restendpoint.http.IOUtils;
import com.github.avarabyeu.restendpoint.http.exception.SerializerException;
import com.github.avarabyeu.restendpoint.serializer.Serializer;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * XML serializer using JAXB. Creates new JAXBContext with each new serializer instances.
 * You should not create several serializer instances for one jaxb context (or take care about JAXBContext caching), because this implementation
 * is thread-safe and doesn't have any internal caches.
 *
 * @author Andrey Vorobyov
 */
public class JaxbSerializer implements Serializer {

    private JAXBContext jaxbContext;

    public JaxbSerializer(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public JaxbSerializer(Class... classes) throws SerializerException {
        try {
            this.jaxbContext = JAXBContext.newInstance(classes);
        } catch (JAXBException e) {
            throw new SerializerException("Unable to create JaxbContext", e);
        }
    }

    public JaxbSerializer(String contextPath) throws SerializerException {
        try {
            this.jaxbContext = JAXBContext.newInstance(contextPath);
        } catch (JAXBException e) {
            throw new SerializerException("Unable to create JaxbContext", e);
        }
    }

    @Override
    public <T> byte[] serialize(T t) throws SerializerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            StreamResult result = new StreamResult(baos);
            jaxbContext.createMarshaller().marshal(t, result);
            return baos.toByteArray();
        } catch (JAXBException e) {
            throw new SerializerException("Unable to serialize xml", e);
        } finally {
            IOUtils.closeQuietly(baos);
        }

    }

    @Override
    public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
        InputStream is;
        try {
            is = new ByteArrayInputStream(content);
            JAXBElement<T> result = jaxbContext.createUnmarshaller().unmarshal(new StreamSource(is), clazz);
            return result.getValue();
        } catch (JAXBException e) {
            throw new SerializerException("Unable to deserialize xml", e);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] content, Type type) throws SerializerException {
        return (T) deserialize(content, TypeToken.of(type).getRawType());
    }

    @Override
    public String getMimeType() {
        return MediaType.APPLICATION_XML_UTF_8.toString();
    }

    @Override
    public boolean canRead(MediaType mimeType, Class<?> resultType) {
        return canRead(mimeType, TypeToken.of(resultType).getType());
    }

    @Override
    public boolean canRead(MediaType mimeType, Type resultType) {
        return mimeType.withoutParameters().is(MediaType.APPLICATION_XML_UTF_8.withoutParameters());
    }

    @Override
    public boolean canWrite(Object o) {
        return null != o && o.getClass().isAnnotationPresent(XmlRootElement.class);
    }
}
