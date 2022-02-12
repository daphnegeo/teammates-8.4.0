package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link CreateCourseAction}.
 */
public class CreateCourseActionTest extends BaseActionTest<CreateCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        String[] submissionParams = new String[] {};

        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
