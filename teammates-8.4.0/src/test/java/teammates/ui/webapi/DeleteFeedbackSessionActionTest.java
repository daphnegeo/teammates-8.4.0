package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteFeedbackSessionAction}.
 */
public class DeleteFeedbackSessionActionTest extends BaseActionTest<DeleteFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    public void testDeleteFeedbackSessionAction_invalidParameters_shouldThrowHttpParameterException() {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("No course ID");
        String[] noCourseIdParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        verifyHttpParameterFailure(noCourseIdParams);

        ______TS("No session name");
        String[] noSessionname = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyHttpParameterFailure(noSessionname);

        ______TS("Empty parameters");
        verifyHttpParameterFailure();
    }

    @Test
    public void testDeleteFeedbackSessionAction_typicalCase_shouldPass() throws Exception {
        ______TS("Delete session that has been soft deleted");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };

        assertNotNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        logic.moveFeedbackSessionToRecycleBin(session.getFeedbackSessionName(), course.getId());
        assertNotNull(logic.getFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), course.getId()));

        DeleteFeedbackSessionAction deleteFeedbackSessionAction = getAction(params);
        JsonResult result = getJsonResult(deleteFeedbackSessionAction);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "The feedback session is deleted.");
        assertNull(logic.getFeedbackSessionFromRecycleBin(session.getFeedbackSessionName(), course.getId()));
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        ______TS("Delete session not in recycle bin");

        FeedbackSessionAttributes session2 = typicalBundle.feedbackSessions.get("session2InCourse1");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session2.getFeedbackSessionName(),
        };

        assertNull(logic.getFeedbackSessionFromRecycleBin(session2.getFeedbackSessionName(), course.getId()));
        assertNotNull(logic.getFeedbackSession(session2.getFeedbackSessionName(), course.getId()));

        deleteFeedbackSessionAction = getAction(params);
        result = getJsonResult(deleteFeedbackSessionAction);
        messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "The feedback session is deleted.");
        assertNull(logic.getFeedbackSessionFromRecycleBin(session2.getFeedbackSessionName(), course.getId()));
        assertNull(logic.getFeedbackSession(session2.getFeedbackSessionName(), course.getId()));
    }

    @Test
    public void testDeleteFeedbackSession_failureCases_shouldFailSilently() {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("Delete session that has already been deleted");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
        };
        assertNotNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));
        DeleteFeedbackSessionAction deleteFeedbackSessionAction = getAction(params);

        // Delete once
        getJsonResult(deleteFeedbackSessionAction);
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        // Delete again
        // Will fail silently and not throw any exception
        getJsonResult(deleteFeedbackSessionAction);
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));

        ______TS("Delete session that does not exist");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName",
        };

        assertNull(logic.getFeedbackSession("randomName", course.getId()));
        deleteFeedbackSessionAction = getAction(params);

        // Will fail silently and not throw any exception
        getJsonResult(deleteFeedbackSessionAction);
        assertNull(logic.getFeedbackSession(session.getFeedbackSessionName(), course.getId()));
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

        logic.moveFeedbackSessionToRecycleBin(session.getFeedbackSessionName(), course.getId());

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

	@Override
	protected EntityAttributes<Account> getAccount(EntityAttributes<Account> account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CourseAttributes getCourse(CourseAttributes course) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityAttributes<FeedbackQuestion> getFeedbackQuestion(EntityAttributes<FeedbackQuestion> fq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected StudentAttributes getStudent(StudentAttributes student) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doRemoveAndRestoreDataBundle(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doPutDocuments(DataBundle testData) {
		// TODO Auto-generated method stub
		return false;
	}
}
