package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link JoinCourseAction}.
 */
public class JoinCourseActionTest extends BaseActionTest<JoinCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
