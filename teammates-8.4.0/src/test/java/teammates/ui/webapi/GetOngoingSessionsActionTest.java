package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.OngoingSessionsData;

/**
 * SUT: {@link GetOngoingSessionsAction}.
 */
public class GetOngoingSessionsActionTest extends BaseActionTest<GetOngoingSessionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS_ONGOING;
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

    private void verifyNoExistingSession(JsonResult r) {
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(0, response.getTotalAwaitingSessions());
        assertEquals(0, response.getTotalOpenSessions());
        assertEquals(0, response.getTotalClosedSessions());
        assertEquals(0, response.getTotalOngoingSessions());
        assertEquals(0, response.getTotalInstitutes());
        assertEquals(0, response.getSessions().size());
    }

}
