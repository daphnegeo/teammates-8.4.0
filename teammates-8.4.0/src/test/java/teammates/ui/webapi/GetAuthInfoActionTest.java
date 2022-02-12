package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.Cookie;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * SUT: {@link GetAuthInfoAction}.
 */
public class GetAuthInfoActionTest extends BaseActionTest<GetAuthInfoAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    public void testExecute_addCsrfTokenCookies_shouldAddToResponseAccordingToExistingCsrfToken() {

        String expectedCsrfToken = StringHelper.encrypt("1234");
        String[] emptyParams = new String[] {};

        ______TS("No logged in user");

        logoutUser();

        GetAuthInfoAction a = getAction(emptyParams);
        JsonResult r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with fake csrf token");

        loginAsInstructor("idOfInstructor1OfCourse1");

        Cookie cookieToAdd = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME, "someFakeCsrfToken");

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with non existing csrf token");

        loginAsInstructor("idOfInstructor1OfCourse1");

        a = getAction(emptyParams);
        r = getJsonResult(a);

        assertEquals(expectedCsrfToken, r.getCookies().get(0).getValue());

        ______TS("User logged in with matched CSRF token cookies");

        loginAsInstructor("idOfInstructor1OfCourse1");

        cookieToAdd = new Cookie(Const.SecurityConfig.CSRF_COOKIE_NAME,
                StringHelper.encrypt("1234"));

        a = getActionWithCookie(new ArrayList<>(Arrays.asList(cookieToAdd)), emptyParams);
        r = getJsonResult(a);

        assertEquals(0, r.getCookies().size());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();

        ______TS("Failure case: Non-admin cannot masquerade");

        loginAsInstructor("idOfInstructor1OfCourse1");
        verifyCannotMasquerade("idOfAnotherInstructor");
    }

}
