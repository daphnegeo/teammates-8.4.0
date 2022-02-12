package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionResendPublishedEmailWorkerAction}.
 */
public class FeedbackSessionResendPublishedEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionResendPublishedEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL;
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
