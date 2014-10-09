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
package com.github.avarabyeu.restendpoint.http.proxy;

import com.github.avarabyeu.restendpoint.async.Will;
import com.google.common.reflect.Invokable;
import com.smarttested.qa.smartassert.SmartAssert;
import org.junit.Test;

import static com.github.avarabyeu.restendpoint.http.proxy.RestMethodInfo.isAsynchronous;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author Andrei Varabyeu
 */
public class RestMethodInfoTest {


    @Test
    public void testSynchronousParser() throws NoSuchMethodException {
        Invokable<?, Object> testSyncronousMethod = Invokable.from(this.getClass().getDeclaredMethod("testSyncronousMethod"));
        Invokable<?, Object> testAsyncronousMethod = Invokable.from(this.getClass().getDeclaredMethod("testAsyncronousMethod"));
        Invokable<?, Object> testVoidMethod = Invokable.from(this.getClass().getDeclaredMethod("testVoidMethod"));
        System.out.println(isAsynchronous(testSyncronousMethod));

        SmartAssert.assertSoft(isAsynchronous(testSyncronousMethod), is(false), "Incorrect synchronous method detection");
        SmartAssert.assertSoft(isAsynchronous(testAsyncronousMethod), is(true), "Incorrect asynchronous method detection");
        SmartAssert.assertSoft(isAsynchronous(testVoidMethod), is(false), "Incorrect void method detection");
    }

    @SuppressWarnings("UnusedDeclaration")
    String testSyncronousMethod() {
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    Will<String> testAsyncronousMethod() {
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    void testVoidMethod() {

    }
}
