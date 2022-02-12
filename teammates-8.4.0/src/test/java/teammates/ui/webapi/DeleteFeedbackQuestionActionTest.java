package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionsVariousAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

/**
 * SUT: {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionTest extends BaseActionTest<DeleteFeedbackQuestionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionsVariousAttributes typicalQuestion =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);

        ______TS("non-existent feedback question");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "randomQuestionId",
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("inaccessible without ModifySessionPrivilege");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
        };

        verifyInaccessibleWithoutModifySessionPrivilege(submissionParams);

        ______TS("only instructors of the same course with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
