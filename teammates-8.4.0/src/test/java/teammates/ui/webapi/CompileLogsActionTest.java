package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link CompileLogsAction}.
 */
public class CompileLogsActionTest extends BaseActionTest<CompileLogsAction> {

    @Override
    protected String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_LOG_COMPILATION;
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
