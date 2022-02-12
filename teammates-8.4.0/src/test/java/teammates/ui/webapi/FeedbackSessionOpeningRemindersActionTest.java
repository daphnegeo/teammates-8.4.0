package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningRemindersAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS;
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
