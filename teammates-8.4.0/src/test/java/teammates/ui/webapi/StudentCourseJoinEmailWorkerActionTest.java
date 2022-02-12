package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link StudentCourseJoinEmailWorkerAction}.
 */
public class StudentCourseJoinEmailWorkerActionTest extends BaseActionTest<StudentCourseJoinEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL;
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
