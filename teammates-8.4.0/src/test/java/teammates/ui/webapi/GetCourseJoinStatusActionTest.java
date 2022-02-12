package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link GetCourseJoinStatusAction}.
 */
public class GetCourseJoinStatusActionTest extends BaseActionTest<GetCourseJoinStatusAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
