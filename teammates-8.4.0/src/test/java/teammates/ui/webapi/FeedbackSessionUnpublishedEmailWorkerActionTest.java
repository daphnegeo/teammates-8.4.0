package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionUnpublishedEmailWorkerAction}.
 */
public class FeedbackSessionUnpublishedEmailWorkerActionTest
        extends BaseActionTest<FeedbackSessionUnpublishedEmailWorkerAction> {

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL;
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
