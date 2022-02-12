package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link CreateFeedbackSessionLogAction}.
 */
public class CreateFeedbackSessionLogActionTest extends BaseActionTest<CreateFeedbackSessionLogAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }
}
