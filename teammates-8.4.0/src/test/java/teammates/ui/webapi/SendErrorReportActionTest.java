package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link SendErrorReportAction}.
 */
public class SendErrorReportActionTest extends BaseActionTest<SendErrorReportAction> {
    private static final String REQUEST_ID = "REQUESTID";
    private static final String SUBJECT = "Email subject";
    private static final String CONTENT = "Email content";
    private static final String[] PARAMS = {};

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ERROR_REPORT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
