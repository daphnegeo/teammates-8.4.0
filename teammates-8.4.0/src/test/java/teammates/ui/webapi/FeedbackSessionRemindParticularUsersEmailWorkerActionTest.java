package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionRemindParticularUsersEmailWorkerAction}.
 */
public class FeedbackSessionRemindParticularUsersEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindParticularUsersEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL;
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
