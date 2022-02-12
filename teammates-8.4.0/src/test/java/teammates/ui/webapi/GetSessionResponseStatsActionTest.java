package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link GetSessionResponseStatsAction}.
 */
public class GetSessionResponseStatsActionTest extends BaseActionTest<GetSessionResponseStatsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_STATS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        ______TS("accessible for admin");
        verifyAccessibleForAdmin();

        ______TS("accessible for authenticated instructor");
        FeedbackSessionAttributes accessibleFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, accessibleFeedbackSession.getFeedbackSessionName(),
                Const.ParamsNames.COURSE_ID, accessibleFeedbackSession.getCourseId(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }

}
