package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link GetFeedbackSessionLogsAction}.
 */
public class GetFeedbackSessionLogsActionTest extends BaseActionTest<GetFeedbackSessionLogsAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testAccessControl() {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes helper = typicalBundle.instructors.get("helperOfCourse1");
        String courseId = instructor.getCourseId();

        ______TS("Only instructors of the same course can access");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);

        ______TS("Only instructors with modify student, session and instructor privilege can access");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
        };

        verifyCannotAccess(submissionParams);

        loginAsInstructor(helper.getGoogleId());
        verifyCannotAccess(submissionParams);

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

}
