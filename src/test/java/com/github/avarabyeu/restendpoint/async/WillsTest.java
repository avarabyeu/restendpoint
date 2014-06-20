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

    public static final String TEST_STRING = "test";

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
}
