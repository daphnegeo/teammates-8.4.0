package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionPublishedEmailWorkerAction}.
 */
public class FeedbackSessionPublishedEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionPublishedEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL;
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
