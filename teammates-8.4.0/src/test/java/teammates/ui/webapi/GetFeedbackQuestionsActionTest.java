package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.ui.output.FeedbackQuestionsData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackQuestionsAction}.
 */
public class GetFeedbackQuestionsActionTest extends BaseActionTest<GetFeedbackQuestionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    public void testExecute_studentFeedbackSubmissionMcqGenerateOptionsForTeams_shouldReturnGeneratedFields()
            throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes studentAttributes = typicalBundle.students.get("student1InCourse1");

        loginAsStudent(studentAttributes.getGoogleId());

        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        String[] params = {
                Const.ParamsNames.COURSE_ID, fsa.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackQuestionsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionsData feedbackQuestionsResponse = (FeedbackQuestionsData) r.getOutput();

        assertEquals(Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2"),
                ((FeedbackMcqQuestionDetails)
                        feedbackQuestionsResponse.getQuestions().get(0).getQuestionDetails()).getMcqChoices());
    }

    @Test
    public void testExecute_instructorFeedbackSubmissionMcqGenerateOptionsForTeams_shouldReturnGeneratedFields()
            throws Exception {
        FeedbackSessionAttributes fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackMcqQuestionDetails feedbackMcqQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackMcqQuestionDetails.setGenerateOptionsFor(FeedbackParticipantType.TEAMS);
        logic.createFeedbackQuestion(FeedbackQuestionAttributes.builder()
                .withCourseId(fsa.getCourseId())
                .withFeedbackSessionName(fsa.getFeedbackSessionName())
                .withNumberOfEntitiesToGiveFeedbackTo(2)
                .withQuestionDescription("test")
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.INSTRUCTORS)
                .withQuestionDetails(feedbackMcqQuestionDetails)
                .withShowResponsesTo(new ArrayList<>())
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build());

        String[] params = {
                Const.ParamsNames.COURSE_ID, fsa.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fsa.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackQuestionsAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        FeedbackQuestionsData feedbackQuestionsResponse = (FeedbackQuestionsData) r.getOutput();

        assertEquals(Arrays.asList("Team 1.1</td></div>'\"", "Team 1.2"),
                ((FeedbackMcqQuestionDetails)
                        feedbackQuestionsResponse.getQuestions().get(0).getQuestionDetails()).getMcqChoices());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for a session",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(params);

        ______TS("only instructors of the same course can access");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(params);

        ______TS("observers of course can access result");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, params);
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
