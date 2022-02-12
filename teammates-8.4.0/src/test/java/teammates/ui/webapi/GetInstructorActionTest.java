package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetInstructorAction}.
 */
public class GetInstructorActionTest extends BaseActionTest<GetInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("only instructors of the same course with correct privilege can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, submissionParams);

        ______TS("feedback session does not exist");

        String[] invalidFeedbackSessionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "TEST_SESSION",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        verifyEntityNotFoundAcl(invalidFeedbackSessionParams);

        ______TS("need login for FULL_DETAILS intent");
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyAnyLoggedInUserCanAccess(submissionParams);
    }

}
