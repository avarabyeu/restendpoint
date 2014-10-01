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
        RestCommand<String, String> command = new RestCommand<String, String>(null, HttpMethod.POST, "hello", String.class);
        SmartAssert.assertHard(command.getResponseType(), is(TypeToken.of(String.class).getType()), "Incorrect class type resolver");
    }


}
