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

package com.epam.reportportal.restendpoint.http;

import com.epam.reportportal.restendpoint.serializer.Serializer;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiPartRequest. Contains part which should be serialized and binary part to
 * be placed in Request
 *
 * @author Andrei Varabyeu
 */
public class MultiPartRequest {

    /**
     * Set of Serialized Parts
     */
    private final List<MultiPartSerialized<?>> serializedRQs;

    /**
     * Set of binary parts
     */
    private final List<MultiPartBinary> binaryRQs;

    public MultiPartRequest(List<MultiPartSerialized<?>> serializedRQs, List<MultiPartBinary> binaryRQs) {
        this.serializedRQs = serializedRQs;
        this.binaryRQs = binaryRQs;
    }

    public final List<MultiPartBinary> getBinaryRQs() {
        return binaryRQs;
    }

    public final List<MultiPartSerialized<?>> getSerializedRQs() {
        return serializedRQs;
    }

    /**
     * Part of request to be serialized (will be serialized using
     * {@link Serializer})
     *
     * @param <RQ> Type of part to be serialized
     * @author Andrei Varabyeu
     * @see Serializer
     */
    public static class MultiPartSerialized<RQ> {

        private final String partName;

        private final RQ request;

        public MultiPartSerialized(String partName, RQ request) {
            this.partName = partName;
            this.request = request;
        }

        public final String getPartName() {
            return partName;
        }

        public final RQ getRequest() {
            return request;
        }
    }

    /**
     * Binary part of multipart request
     * (won't be serialized using {@link Serializer})
     *
     * @author Andrei Varabyeu
     * @see Serializer
     */
    public static class MultiPartBinary {
        private final String partName;
        private final String filename;
        private final String contentType;
        private final ByteSource data;

        public MultiPartBinary(String partName, String filename, String contentType, ByteSource data) {
            this.partName = partName;
            this.filename = filename;
            this.data = data;
            this.contentType = contentType;
        }

        public final ByteSource getData() {
            return data;
        }

        public final String getFilename() {
            return filename;
        }

        public final String getPartName() {
            return partName;
        }

        public final String getContentType() {
            return contentType;
        }

    }

    /**
     * Builder for multipart requests
     *
     * @author Andrei Varabyeu
     */
    public static class Builder {
        private final List<MultiPartSerialized<?>> serializedRQs;

        private final List<MultiPartBinary> binaryRQs;

        public Builder() {
            serializedRQs = new ArrayList<MultiPartSerialized<?>>();
            binaryRQs = new ArrayList<MultiPartBinary>();
        }

        /**
         * Adds part of request which is going to be serialized
         *
         * @param partName Part Name
         * @param body     Part Body
         * @param <RQ>     Type of body
         * @return This instance
         */
        public <RQ> Builder addSerializedPart(String partName, RQ body) {
            serializedRQs.add(new MultiPartSerialized<RQ>(partName, body));
            return this;
        }

        /**
         * Adds part which is NOT going to be serialized
         *
         * @param partName    Part Name
         * @param filename    Name of file in multipart request
         * @param contentType Content Type of this part
         * @param data        Part data
         * @return This instance
         */
        public Builder addBinaryPart(String partName, String filename, String contentType, @Nonnull ByteSource data) {
            Preconditions.checkNotNull(data, "Provided data shouldn't be null");
            binaryRQs.add(new MultiPartBinary(partName, filename, contentType, data));
            return this;
        }

        /**
         * Builds {@link MultiPartRequest}
         *
         * @return Built Multipart Request
         */
        public MultiPartRequest build() {
            return new MultiPartRequest(serializedRQs, binaryRQs);
        }
    }
}
