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

package com.github.avarabyeu.restendpoint.http;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

/**
 * MultiPartRequest. Contains part which should be serialized and binary part to
 * be placed in Request
 * 
 * @author Andrei Varabyeu
 * 
 * @param <RQ>
 *            - Type of request to be serialized
 */
public class MultiPartRequest<RQ> {

	/** Set of Serialized Parts */
	private List<MultiPartSerialized<RQ>> serializedRQs;

	/** Set of binary parts */
	private List<MultiPartBinary> binaryRQs;

	public MultiPartRequest(List<MultiPartSerialized<RQ>> serializedRQs, List<MultiPartBinary> binaryRQs) {
		this.serializedRQs = serializedRQs;
		this.binaryRQs = binaryRQs;
	}

	public List<MultiPartBinary> getBinaryRQs() {
		return binaryRQs;
	}

	public List<MultiPartSerialized<RQ>> getSerializedRQs() {
		return serializedRQs;
	}

	/**
	 * Part of request to be serialized
	 * 
	 * @author Andrei Varabyeu
	 * 
	 * @param <RQ>
	 */
	public static class MultiPartSerialized<RQ> {

		private String partName;

		private RQ request;

		public MultiPartSerialized(String partName, RQ request) {
			this.partName = partName;
			this.request = request;
		}

		public String getPartName() {
			return partName;
		}

		public RQ getRequest() {
			return request;
		}
	}

	/**
	 * Binary part of request
	 * 
	 * @author Andrei Varabyeu
	 * 
	 */
	public static class MultiPartBinary {
		private String partName;
		private String filename;
		private String contentType;
		private ByteSource data;

		public MultiPartBinary(String partName, String filename, String contentType, ByteSource data) {
			this.partName = partName;
			this.filename = filename;
			this.data = data;
			this.contentType = contentType;
		}

		public ByteSource getData() {
			return data;
		}

		public String getFilename() {
			return filename;
		}

		public String getPartName() {
			return partName;
		}

		public String getContentType() {
			return contentType;
		}

	}

	/**
	 * Builder for multipart requests
	 * 
	 * @author Andrei Varabyeu
	 * 
	 * @param <RQ>
	 */
	public static class Builder<RQ> {
		private List<MultiPartSerialized<RQ>> serializedRQs;

		private List<MultiPartBinary> binaryRQs;

		public Builder() {
			serializedRQs = new ArrayList<MultiPartRequest.MultiPartSerialized<RQ>>();
			binaryRQs = new ArrayList<MultiPartRequest.MultiPartBinary>();
		}

		public Builder<RQ> addSerializedPart(String partName, RQ body) {
			serializedRQs.add(new MultiPartSerialized<RQ>(partName, body));
			return this;
		}

		public Builder<RQ> addBinaryPart(String partName, String filename, String contentType, @Nonnull ByteSource data) {
			Preconditions.checkNotNull(data, "Provided data shouldn't be null");
			binaryRQs.add(new MultiPartBinary(partName, filename, contentType, data));
			return this;
		}

		public MultiPartRequest<RQ> build() {
			return new MultiPartRequest<RQ>(serializedRQs, binaryRQs);
		}
	}
}
