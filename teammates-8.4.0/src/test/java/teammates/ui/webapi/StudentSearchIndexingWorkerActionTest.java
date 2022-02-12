package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const.TaskQueue;

/**
 * SUT: {@link StudentSearchIndexingWorkerAction}.
 */
public class StudentSearchIndexingWorkerActionTest extends BaseActionTest<StudentSearchIndexingWorkerAction> {

    @Override
    protected String getActionUri() {
        return TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
