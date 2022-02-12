package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link GetAccountAction}.
 */
public class GetAccountActionTest extends BaseActionTest<GetAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
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
