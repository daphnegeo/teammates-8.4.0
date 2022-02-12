package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link GetNationalitiesAction}.
 */
public class GetNationalitiesActionTest extends BaseActionTest<GetNationalitiesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.NATIONALITIES;
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
