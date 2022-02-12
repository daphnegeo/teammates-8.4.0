package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosedRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
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
