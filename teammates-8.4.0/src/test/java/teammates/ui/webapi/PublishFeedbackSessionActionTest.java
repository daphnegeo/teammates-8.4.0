package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest extends BaseActionTest<PublishFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_PUBLISH;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    public void testExecute_invalidRequests_shouldFail() {
        ______TS("non existent session name");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        String randomSessionName = "randomName";

        assertNotNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, randomSessionName,
        };

        assertNull(logic.getFeedbackSession(randomSessionName, course.getId()));

        EntityNotFoundException enfe = verifyEntityNotFound(params);
        assertEquals("Feedback session not found", enfe.getMessage());

        ______TS("non existent course id");

        String randomCourseId = "randomCourseId";

        params = new String[] {
                Const.ParamsNames.COURSE_ID, randomCourseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), randomCourseId));

        enfe = verifyEntityNotFound(params);
        assertEquals("Feedback session not found", enfe.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }
}
