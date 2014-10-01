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

package com.github.avarabyeu.restendpoint.async;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

/**
 * Tests {@link com.github.avarabyeu.restendpoint.async.Will} functionality
 *
 * @author avarabyeu
 */
public class WillsTest {

    private static final String TEST_STRING = "test";

    @Test
    public void testWill() {
        Will<String> will = Wills.will(TEST_STRING);

        Assert.assertThat(will.isDone(), is(true));
        Assert.assertThat(will.isCancelled(), is(false));
        Assert.assertThat(will.obtain(), is(TEST_STRING));
    }

    @Test
    public void testMap() {
        Will<String> will = Wills.will(TEST_STRING).map(new Function<String, String>() {

            @Nullable
            @Override
            public String apply(@Nullable String input) {
                assert input != null;
                return input.toUpperCase();
            }
        });


        Assert.assertThat(will.obtain(), is(TEST_STRING.toUpperCase()));
    }

    @Test
    public void testWhenDone() {
        final List<String> results = Lists.newArrayList();
        Will<String> will = Wills.will(TEST_STRING).whenDone(new Action<String>() {
            @Override
            public void apply(String s) {
                results.add(s);
            }
        });
        /* waits for done */
        will.obtain();
        Assert.assertThat(results, hasItem(TEST_STRING));
    }

    @Test
    public void testWhenFailed() {
        final List<Throwable> results = Lists.newArrayList();
        RuntimeException throwable = new RuntimeException("");
        Will<String> will = Wills.failedWill(throwable, String.class).whenFailed(new Action<Throwable>() {
            @Override
            public void apply(Throwable throwable) {
                results.add(throwable);
            }
        });


        try {
            /* waits for done */
            will.obtain();
        } catch (RuntimeException e) {
            Assert.assertThat(e, is(throwable));
        }
        Assert.assertThat(results, hasItem(throwable));
    }
}
