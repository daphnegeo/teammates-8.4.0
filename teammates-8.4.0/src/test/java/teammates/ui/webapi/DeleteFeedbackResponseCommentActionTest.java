package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
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
import teammates.ui.request.Intent;

/**
 * SUT: {@link DeleteFeedbackResponseCommentAction}.
 */
public class DeleteFeedbackResponseCommentActionTest extends BaseActionTest<DeleteFeedbackResponseCommentAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeMethod
    protected void refreshTestData() {
        typicalBundle = loadDataBundle("/FeedbackResponseCommentCRUDTest.json");
        removeAndRestoreDataBundle(typicalBundle);
    }

    @Override
    protected void testAccessControl() {
        // See each independent test case
    }

    @Test
    protected void testAccessControlsForCommentByInstructor() throws Exception {
        int questionNumber = 1;
        CourseAttributes course = typicalBundle.courses.get("idOfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromInstructor1Q2");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ1");

        EntityAttributes<FeedbackQuestion> question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());
        comment.setFeedbackResponseId(response.getId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, String.valueOf(comment.getId()),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        ______TS("Comment giver without privilege should pass");

        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();

        logic.updateInstructor(InstructorAttributes.updateOptionsWithEmailBuilder(course.getId(), instructor1.getEmail())
                .withPrivileges(instructorPrivileges).build());

        loginAsInstructor(instructor1.getGoogleId());
        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor1, submissionParams);

        ______TS("Instructor with correct privilege should pass");

        InstructorAttributes instructor2 = typicalBundle.instructors.get("instructor2OfCourse1");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor2.getGoogleId());
        verifyCanAccess(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor2, submissionParams);

        ______TS("Instructor with only section 1 privilege should fail");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A"});
        verifyCannotAccess(submissionParams);

        ______TS("Instructor with only section 2 privilege should fail");

        grantInstructorWithSectionPrivilege(instructor2,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section B"});
        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByInstructorAsFeedbackParticipant() {
        int questionNumber = 1;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromInstructor1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ1");

        comment = feedbackQuestionAttributesmethod(questionNumber, fs, comment, response);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };

        ______TS("Instructor who give the comment can delete comment");

        InstructorAttributes instructorWhoGiveComment = typicalBundle.instructors.get("instructor1OfCourse1");
        assertEquals(instructorWhoGiveComment.getEmail(), comment.getCommentGiver());
        loginAsInstructor(instructorWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different instructor of same course cannot delete comment");

        InstructorAttributes differentInstructorInSameCourse = typicalBundle.instructors.get("instructor2OfCourse1");
        assertNotEquals(differentInstructorInSameCourse.getEmail(), comment.getCommentGiver());
        loginAsInstructor(differentInstructorInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

	/**
	 * @param questionNumber
	 * @param fs
	 * @param comment
	 * @param response
	 * @return
	 */
	private FeedbackResponseCommentAttributes feedbackQuestionAttributesmethod(int questionNumber,
			FeedbackSessionAttributes fs, FeedbackResponseCommentAttributes comment,
			FeedbackResponseAttributes response) {
		EntityAttributes<FeedbackQuestion> question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());
		return comment;
	}

    @Test
    public void testAccessControlsForCommentByStudent() {
        int questionNumber = 3;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromStudent1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ3");

        comment = feedbackQuestionAttributesmethod(questionNumber, fs, comment, response);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Student who give the comment can delete comment");

        StudentAttributes studentWhoGiveComment = typicalBundle.students.get("student1InCourse1");
        assertEquals(studentWhoGiveComment.getEmail(), comment.getCommentGiver());
        loginAsStudent(studentWhoGiveComment.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Different student of same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = typicalBundle.students.get("student2InCourse1");
        assertNotEquals(differentStudentInSameCourse.getEmail(), comment.getCommentGiver());
        loginAsStudent(differentStudentInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Typical cases: unauthorized users");

        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
    }

    @Test
    public void testCrossSectionAccessControl() throws Exception {
        int questionNumber = 6;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment2FromStudent1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ6");

        comment = feedbackQuestionAttributesmethod(questionNumber, fs, comment, response);

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Instructor with correct privilege can delete comment");

        InstructorAttributes instructor = typicalBundle.instructors.get("helperOfCourse1");

        String[] instructorParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(instructorParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(instructor, instructorParams);

        ______TS("Instructor with only section A privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A"});

        verifyCannotAccess(submissionParams);

        ______TS("Instructor with only section B privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section B"});

        verifyCannotAccess(submissionParams);
    }

    @Test
    public void testAccessControlsForCommentByTeam() throws Exception {
        int questionNumber = 4;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackResponseCommentAttributes comment = typicalBundle.feedbackResponseComments.get("comment1FromTeam1");
        FeedbackResponseAttributes response = typicalBundle.feedbackResponses.get("response1ForQ4");

        EntityAttributes<FeedbackQuestion> question =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        assertEquals(FeedbackParticipantType.TEAMS, question.getGiverType());
        response = logic.getFeedbackResponse(question.getId(), response.getGiver(), response.getRecipient());
        comment = logic.getFeedbackResponseComment(response.getId(), comment.getCommentGiver(), comment.getCreatedAt());

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };

        ______TS("Different student of different team and same course cannot delete comment");

        StudentAttributes differentStudentInSameCourse = typicalBundle.students.get("student3InCourse1");
        assertNotEquals(differentStudentInSameCourse.getTeam(), response.getGiver());
        loginAsStudent(differentStudentInSameCourse.getGoogleId());
        verifyCannotAccess(submissionParams);

        ______TS("Different student of same team can delete comment");

        StudentAttributes differentStudentInSameTeam = typicalBundle.students.get("student2InCourse1");
        assertEquals(differentStudentInSameTeam.getTeam(), response.getGiver());
        loginAsStudent(differentStudentInSameTeam.getGoogleId());
        verifyCanAccess(submissionParams);

        ______TS("Typical cases: unauthorized users");

        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        ______TS("Instructor with correct privilege can delete comment");

        String[] instructorParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.getId().toString(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        InstructorAttributes instructor = typicalBundle.instructors.get("helperOfCourse1");
        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A", "Section B"});

        loginAsInstructor(instructor.getGoogleId());
        verifyCanAccess(instructorParams);
        verifyCanMasquerade(instructor.getGoogleId(), instructorParams);

        ______TS("Instructor with only section A privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section A"});

        verifyCannotAccess(submissionParams);

        ______TS("Instructor with only section B privilege cannot delete comment");

        grantInstructorWithSectionPrivilege(instructor,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section B"});

        verifyCannotAccess(submissionParams);
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
