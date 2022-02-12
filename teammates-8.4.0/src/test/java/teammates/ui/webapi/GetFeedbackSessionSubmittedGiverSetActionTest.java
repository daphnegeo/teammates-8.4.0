package teammates.ui.webapi;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link GetFeedbackSessionSubmittedGiverSetAction}.
 */
public class GetFeedbackSessionSubmittedGiverSetActionTest
        extends BaseActionTest<GetFeedbackSessionSubmittedGiverSetAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testAccessControl() {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
