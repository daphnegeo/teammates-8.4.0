package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
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
import teammates.ui.output.CommentVisibilityType;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.Intent;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * SUT: {@link UpdateFeedbackResponseCommentAction}.
 */
public class UpdateFeedbackResponseCommentActionTest extends BaseActionTest<UpdateFeedbackResponseCommentAction> {

    public CourseAttributes course;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    public InstructorAttributes helperOfCourse1;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private StudentAttributes student3InCourse1;
    private FeedbackResponseAttributes response1ForQn1;
    private FeedbackResponseCommentAttributes comment1FromInstructor1;
    private FeedbackResponseCommentAttributes comment1FromStudent1;
    private FeedbackResponseCommentAttributes comment2FromStudent1;
    private FeedbackResponseCommentAttributes comment1FromTeam1;
    private FeedbackResponseCommentAttributes comment1FromInstructor1Q2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    protected void testExecute_notEnoughParams_shouldFail() {

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_typicalSuccessfulCases_shouldPass() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.getCommentGiver(), comment1FromInstructor1.getCreatedAt());
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for INSTRUCTOR_RESULT");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(comment1FromInstructor1.getId());
        assertEquals(comment1FromInstructor1.getCommentText() + " (Edited)", frc.getCommentText());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.getCommentGiverType());
        assertEquals(instructor1OfCourse1.getEmail(), frc.getCommentGiver());
        assertTrue(frc.isCommentFromFeedbackParticipant());

        ______TS("Typical successful case for STUDENT_SUBMISSION");
        loginAsStudent(student1InCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromStudent1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS), Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromStudent1.getId());
        assertEquals(comment1FromStudent1.getCommentText() + " (Edited)", frc.getCommentText());
        assertEquals(FeedbackParticipantType.STUDENTS, frc.getCommentGiverType());
        assertEquals(student1InCourse1.getEmail(), frc.getCommentGiver());
        assertTrue(frc.isCommentFromFeedbackParticipant());

        ______TS("Typical successful case for INSTRUCTOR_SUBMISSION");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.INSTRUCTORS), Arrays.asList(CommentVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromInstructor1.getId());
        assertEquals(comment1FromInstructor1.getCommentText() + " (Edited)", frc.getCommentText());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.getCommentGiverType());
        assertEquals(instructor1OfCourse1.getEmail(), frc.getCommentGiver());
        assertTrue(frc.isCommentFromFeedbackParticipant());
    }

    @Test
    protected void testExecute_emptyVisibilitySettings_shouldPass() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.getCommentGiver(), comment1FromInstructor1.getCreatedAt());
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Null show comments and show giver permissions");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_variousVisibilitySettings_shouldPass() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.getCommentGiver(), comment1FromInstructor1.getCreatedAt());
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for unpublished session public to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.STUDENTS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_nonExistingFeedbackResponse_shouldFail() {

        ______TS("Non-existent feedback response comment id");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        verifyEntityNotFound(requestBody, submissionParams);
    }

    @Test
    protected void testExecute_instructorIsNotCommentGiver_shouldPass() {
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        ______TS("Instructor is not feedback response comment giver");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1Q2.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1Q2.getCommentText() + " (Edited)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1FromInstructor1Q2.getId());
        assertEquals(comment1FromInstructor1Q2.getCommentText() + " (Edited)", frc.getCommentText());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.getCommentGiverType());
        assertEquals(instructor1OfCourse1.getEmail(), frc.getCommentGiver());
        assertEquals(instructor2OfCourse1.getEmail(), frc.getLastEditorEmail());
        assertFalse(frc.isCommentFromFeedbackParticipant());
        assertEquals(
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS), frc.getShowCommentTo());
        assertEquals(
                Arrays.asList(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS), frc.getShowGiverNameTo());
    }

    @Test
    protected void testExecute_typicalCasePublishedSession_shouldPass() throws Exception {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for published session");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        logic.publishFeedbackSession(
                comment1FromInstructor1Q2.getFeedbackSessionName(), comment1FromInstructor1Q2.getCourseId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1Q2.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromInstructor1Q2.getCommentText() + " (Edited for published session)",
                Arrays.asList(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1FromInstructor1Q2.getId());
        assertEquals(comment1FromInstructor1Q2.getCommentText() + " (Edited for published session)",
                frc.getCommentText());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.getCommentGiverType());
        assertEquals(instructor1OfCourse1.getEmail(), frc.getCommentGiver());
        assertFalse(frc.isCommentFromFeedbackParticipant());
    }

    @Test
    protected void testExecute_emptyCommentText_shouldFail() {
        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.getCommentGiver(), comment1FromInstructor1.getCreatedAt());
        assertNotNull("response comment not found", comment1FromInstructor1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: empty comment text");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                "", new ArrayList<>(), new ArrayList<>());
        InvalidHttpRequestBodyException ihrbe = verifyHttpRequestBodyFailure(requestBody, submissionParams);
        assertEquals(BasicCommentSubmissionAction.FEEDBACK_RESPONSE_COMMENT_EMPTY, ihrbe.getMessage());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        ______TS("invalid intent STUDENT_RESULT");
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment1FromInstructor1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent2);
    }

    @Override
    @Test
    protected void testAccessControl() {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_accessibleWithPrivilege_shouldPass() {

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1Q2.getId().toString(),
        };
        ______TS("accessible for instructors of the same course");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);
        loginAsAdmin();
        verifyCanMasquerade(instructor2OfCourse1.getGoogleId(), submissionParams);

        ______TS("inaccessible for helper instructors");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_typicalSuccessfulCase_shouldPass() {
        ______TS("successful case for instructor result");

        comment1FromInstructor1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1FromInstructor1.getCommentGiver(), comment1FromInstructor1.getCreatedAt());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        verifyHttpParameterFailureAcl(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        verifyHttpParameterFailureAcl(invalidIntent2);
    }

    @Test
    protected void testAccessControl_updateCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student2InCourse1.getGoogleId());
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromStudent1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("students own comments");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParamsStudentToStudents);

        ______TS("students access other students team comments");
        loginAsStudent(student3InCourse1.getGoogleId());
        String[] submissionParamsTeam = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromTeam1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsTeam);

        ______TS("students own team comments");
        loginAsStudent(student1InCourse1.getGoogleId());
        verifyCanAccess(submissionParamsTeam);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromInstructor1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsInstructorToInstructor);

        ______TS("instructors own comments");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyCanAccess(submissionParamsInstructorToInstructor);

    }

    @Test
    protected void testAccessControl_nonExistingResponseComment_shouldFail() {

        ______TS("Response comment doesn't exist");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };
        verifyEntityNotFoundAcl(submissionParams);
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes#testAccessControl_instructorsWithCorrectPrivilege_shouldPass(teammates.ui.webapi.UpdateFeedbackResponseCommentActionTest)} instead
	 */
	@Test
	protected void testAccessControl_instructorsWithCorrectPrivilege_shouldPass() throws Exception {
		comment1FromInstructor1.testAccessControl_instructorsWithCorrectPrivilege_shouldPass(this);
	}

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes#testAccessControl_instructorWithOnlyEitherSectionPrivilege_shouldFail(teammates.ui.webapi.UpdateFeedbackResponseCommentActionTest)} instead
	 */
	@Test
	protected void testAccessControl_instructorWithOnlyEitherSectionPrivilege_shouldFail() throws Exception {
		comment1FromInstructor1.testAccessControl_instructorWithOnlyEitherSectionPrivilege_shouldFail(this);
	}

    public String[] getSubmissionParamsForCrossSectionResponseComment() {
        return new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment2FromStudent1.getId().toString(),
        };
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
