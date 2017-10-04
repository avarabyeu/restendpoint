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

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;
import com.smarttested.qa.smartassert.junit.SoftAssertVerifier;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.smarttested.qa.smartassert.SmartAssert.assertSoft;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for {@link MultiPartRequest} and it's body
 *
 * @author Andrey Vorobyov
 */
public class MultipartRequestTest {

	@Rule
	public SoftAssertVerifier verifier = SoftAssertVerifier.instance();

	@Test
	public void testMultipartRequest() throws IOException {
		MultiPartRequest multiPartRequest = new MultiPartRequest.Builder().
				addBinaryPart("binary part", "filename.txt", MediaType.OCTET_STREAM.toString(), ByteSource.wrap("here is body".getBytes())).
				addSerializedPart("serialized part", "this part will be serialized using serializer").
				build();

		assertSoft(multiPartRequest.getBinaryRQs(), not(empty()), "Binary part is not added");
		MultiPartRequest.MultiPartBinary multiPartBinary = multiPartRequest.getBinaryRQs().get(0);

		assertSoft(multiPartBinary.getContentType(), is(MediaType.OCTET_STREAM.toString()), "Incorrect Media Type");
		assertSoft(multiPartBinary.getFilename(), is("filename.txt"), "Incorrect File Name");
		assertSoft(multiPartBinary.getPartName(), is("binary part"), "Incorrect Part Name");
		assertSoft(multiPartBinary.getData().asCharSource(Charsets.UTF_8).read(), is("here is body"), "Incorrect Body");

		assertSoft(multiPartRequest.getSerializedRQs(), not(empty()), "Serialized part is not added");
		MultiPartRequest.MultiPartSerialized<?> serializedPart = multiPartRequest.getSerializedRQs().get(0);
		assertSoft(serializedPart.getPartName(), is("serialized part"), "Serialized part name is incorrect");
		assertSoft(serializedPart.getRequest(), instanceOf(String.class), "Incorrect serialized part body type");
		assertSoft(
				(String) serializedPart.getRequest(),
				is("this part will be serialized using serializer"),
				"Incorrect serialized part body "
		);
	}

}
