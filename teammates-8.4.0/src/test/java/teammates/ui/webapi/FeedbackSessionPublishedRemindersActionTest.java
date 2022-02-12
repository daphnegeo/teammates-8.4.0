package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionTest
        extends BaseActionTest<FeedbackSessionPublishedRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
