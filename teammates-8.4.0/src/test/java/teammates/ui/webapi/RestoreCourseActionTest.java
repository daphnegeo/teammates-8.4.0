package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;

/**
 * SUT: {@link RestoreCourseAction}.
 */
public class RestoreCourseActionTest
        extends BaseActionTest<RestoreCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute(teammates.ui.webapi.RestoreCourseActionTest)} instead
	 */
	@Override
	@Test
	public void testExecute() throws Exception {
		typicalBundle.testExecute(this);
	}

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "idOfTypicalCourse1",
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_COURSE, submissionParams);
    }
}
