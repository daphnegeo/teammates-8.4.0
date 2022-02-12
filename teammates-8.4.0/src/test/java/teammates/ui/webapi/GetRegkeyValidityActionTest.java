package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link GetRegkeyValidityAction}.
 */
public class GetRegkeyValidityActionTest extends BaseActionTest<GetRegkeyValidityAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH_REGKEY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
