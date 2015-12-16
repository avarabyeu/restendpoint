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

import com.google.common.reflect.TypeToken;
import com.smarttested.qa.smartassert.SmartAssert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author Andrei Varabyeu
 */
public class RestCommandTest {

    @Test
    public void testGenericTypes() {
        RestCommand<String, String> command = new RestCommand<>("", HttpMethod.POST, "hello", String.class);
        SmartAssert.assertHard(command.getResponseType(), is(TypeToken.of(String.class).getType()), "Incorrect class type resolver");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetWithBody() {
        new RestCommand<>("", HttpMethod.GET, "hello", String.class);
    }


}
