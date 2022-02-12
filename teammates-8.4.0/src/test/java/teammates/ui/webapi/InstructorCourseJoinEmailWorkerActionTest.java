package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link InstructorCourseJoinEmailWorkerAction}.
 */
public class InstructorCourseJoinEmailWorkerActionTest
        extends BaseActionTest<InstructorCourseJoinEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
