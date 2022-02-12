package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const.TaskQueue;

/**
 * SUT: {@link InstructorSearchIndexingWorkerAction}.
 */
public class InstructorSearchIndexingWorkerActionTest extends BaseActionTest<InstructorSearchIndexingWorkerAction> {

    @Override
    protected String getActionUri() {
        return TaskQueue.INSTRUCTOR_SEARCH_INDEXING_WORKER_URL;
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
