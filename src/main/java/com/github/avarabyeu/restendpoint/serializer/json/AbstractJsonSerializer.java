package com.github.avarabyeu.restendpoint.serializer.json;

import com.github.avarabyeu.restendpoint.serializer.Serializer;
import com.google.common.net.MediaType;

/**
 * Abstract type for all JSON serializers
 *
 * @author Andrei Varabyeu
 */
abstract class AbstractJsonSerializer implements Serializer {

    @Override
    public String getMimeType() {
        return MediaType.JSON_UTF_8.toString();
    }

    @Override
    public boolean canRead(MediaType mimeType) {
        return MediaType.JSON_UTF_8.withoutParameters().is(mimeType.withoutParameters());
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
