package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link QueryLogsAction}.
 */
public class QueryLogsActionTest extends BaseActionTest<QueryLogsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyAccessibleForAdmin();
        verifyAccessibleForMaintainers();
        verifyInaccessibleForStudents();
        verifyInaccessibleForInstructors();
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
