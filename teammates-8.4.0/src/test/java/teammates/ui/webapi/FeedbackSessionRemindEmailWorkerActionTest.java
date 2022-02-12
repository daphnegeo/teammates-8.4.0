package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionRemindEmailWorkerAction}.
 */
public class FeedbackSessionRemindEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionRemindEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL;
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
