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
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetFeedbackSessionAction}.
 */
public class GetFeedbackSessionActionTest extends BaseActionTest<GetFeedbackSessionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testAccessControl() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        ______TS("non-existent feedback session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "randomName for Session123",
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyEntityNotFoundAcl(submissionParams);

        ______TS("only instructors of the same course can access full detail");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        ______TS("only students of the same course can access student result");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
        };

        verifyAccessibleForStudentsOfTheSameCourse(submissionParams);
        verifyInaccessibleForStudentsOfOtherCourse(submissionParams);

        ______TS("only instructors of the same course can access instructor result");

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };

        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
    }

    @Test
    protected void testAccessControl_studentResult() throws Exception {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.STUDENT_SUBMISSION;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");

        ______TS("Typical unauthorized cases");

        verifyInaccessibleWithoutLogin(params);
        String unregUserId = "unreg.user";
        loginAsUnregistered(unregUserId);
        verifyCannotAccess(params);

        ______TS("student can access his own course session");

        verifyAccessibleForStudentsOfTheSameCourse(params);

        ______TS("Instructor cannot directly get student session");

        loginAsInstructor(typicalBundle.instructors.get("instructor1OfCourse1").getGoogleId());
        verifyCannotAccess(params);

        ______TS("student cannot access other course session");
        FeedbackSessionAttributes otherCourseFeedbackSession = typicalBundle.feedbackSessions.get("session1InCourse2");
        params = generateParameters(otherCourseFeedbackSession, intent, "", "", "");
        verifyCannotAccess(params);

        ______TS("Instructor with correct privilege moderate student session");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        params = generateParameters(feedbackSession, intent, "", student1InCourse1.getEmail(), "");

        verifyInaccessibleForInstructorsOfOtherCourses(params);
        verifyInaccessibleForStudents(params);

        InstructorAttributes helperOfCourse1 = typicalBundle.instructors.get("helperOfCourse1");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(params);

        grantInstructorWithSectionPrivilege(helperOfCourse1,
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                new String[] {"Section 1"});
        verifyCanAccess(params);
        verifyAccessibleForAdminToMasqueradeAsInstructor(helperOfCourse1, params);

        ______TS("Instructor preview student session");
        params = generateParameters(feedbackSession, intent, "", "", student1InCourse1.getEmail());

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }

    @Test
    protected void testAccessControl_fullDetail() {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = generateParameters(feedbackSession, Intent.FULL_DETAIL, "", "", "");
        verifyOnlyInstructorsOfTheSameCourseCanAccess(params);
    }

    @Test
    protected void testAccessControl_instructorResult() throws Exception {
        FeedbackSessionAttributes feedbackSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        Intent intent = Intent.INSTRUCTOR_RESULT;
        String[] params = generateParameters(feedbackSession, intent, "", "", "");
        ______TS("Only instructor with correct privilege can access");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, params
        );

        ______TS("Instructor moderates instructor submission with correct privilege will pass");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        params = generateParameters(feedbackSession, Intent.INSTRUCTOR_SUBMISSION, "", instructor1OfCourse1.getEmail(), "");

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, params);

        ______TS("Instructor preview instructor result with correct privilege will pass");

        String[] previewInstructorSubmissionParams =
                generateParameters(feedbackSession, Intent.INSTRUCTOR_SUBMISSION,
                        "", "", instructor1OfCourse1.getEmail());
        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, previewInstructorSubmissionParams);
    }

    private String[] generateParameters(FeedbackSessionAttributes session, Intent intent,
                                        String regKey, String moderatedPerson, String previewPerson) {
        return new String[] {
                Const.ParamsNames.COURSE_ID, session.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.getFeedbackSessionName(),
                Const.ParamsNames.INTENT, intent.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPerson,
                Const.ParamsNames.PREVIEWAS, previewPerson,
                Const.ParamsNames.REGKEY, regKey,
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
