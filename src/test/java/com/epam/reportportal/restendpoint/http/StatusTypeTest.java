package com.epam.reportportal.restendpoint.http;

import com.smarttested.qa.smartassert.SmartAssert;
import com.smarttested.qa.smartassert.junit.SoftAssertVerifier;
import org.junit.Rule;
import org.junit.Test;

import static com.epam.reportportal.restendpoint.http.StatusType.*;
import static org.hamcrest.CoreMatchers.is;

/**
 * Tests for {@link StatusType}
 *
 * @author Andrei Varabyeu
 */
public class StatusTypeTest {

    @Rule
    public SoftAssertVerifier verifier = SoftAssertVerifier.instance();

    @Test
    public void testInformational() {
        SmartAssert.assertSoft(StatusType.valueOf(100), is(INFORMATIONAL), "'Continue' status is not parsed");
        SmartAssert.assertSoft(StatusType.valueOf(105), is(INFORMATIONAL), "'Name Not Resolved' status is not parsed");
    }

    @Test
    public void testSuccess() {
        SmartAssert.assertSoft(StatusType.valueOf(200), is(SUCCESSFUL), "'OK' status is not parsed");
        SmartAssert.assertSoft(StatusType.valueOf(201), is(SUCCESSFUL), "'Created' status is not parsed");
    }

    @Test
    public void testRedirection() {
        SmartAssert.assertSoft(StatusType.valueOf(300), is(REDIRECTION), "'Multiple Choices' status is not parsed");
        SmartAssert.assertSoft(StatusType.valueOf(302), is(REDIRECTION), "'Found' status is not parsed");
    }

    @Test
    public void testClientError() {
        SmartAssert.assertSoft(StatusType.valueOf(400), is(CLIENT_ERROR), "'Bad Request' status is not parsed");
        SmartAssert.assertSoft(StatusType.valueOf(401), is(CLIENT_ERROR), "'Unauthorized' status is not parsed");
    }

    @Test
    public void testServerError() {
        SmartAssert.assertSoft(StatusType.valueOf(500), is(SERVER_ERROR), "'Internal Server Error' status is not parsed");
        SmartAssert.assertSoft(StatusType.valueOf(502), is(SERVER_ERROR), "'Bad Gateway' status is not parsed");
    }
}
