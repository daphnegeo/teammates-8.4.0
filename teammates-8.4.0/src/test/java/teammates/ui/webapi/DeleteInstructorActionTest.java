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
 * SUT: {@link DeleteInstructorAction}.
 */
public class DeleteInstructorActionTest extends BaseActionTest<DeleteInstructorAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    protected void testExecute_typicalCaseByGoogleId_shouldPass() {
        ______TS("Typical case: admin deletes an instructor by google id");

        loginAsAdmin();

        InstructorAttributes instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");
        String instructorId = instructor1OfCourse2.getGoogleId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor1OfCourse2.getCourseId(),
        };

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor1OfCourse2.getCourseId(), instructor1OfCourse2.getEmail()));

        ______TS("Typical case: instructor deletes another instructor by google id");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor2OfCourse1.getGoogleId(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertTrue(logic.getInstructorsForCourse(instructor1OfCourse1.getCourseId()).size() > 1);

        deleteInstructorAction = getAction(submissionParams);
        response = getJsonResult(deleteInstructorAction);

        msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor2OfCourse1.getCourseId(), instructor2OfCourse1.getEmail()));
        assertNotNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));

    }

    @Test
    public void testExecute_deleteInstructorByEmail_shouldSuccess() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor2OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        deleteEmailActions(instructor1OfCourse1, instructor2OfCourse1, submissionParams);
    }

	/**
	 * @param instructor1OfCourse1
	 * @param instructor2OfCourse1
	 * @param submissionParams
	 */
	private void deleteEmailActions(InstructorAttributes instructor1OfCourse1,
			InstructorAttributes instructor2OfCourse1, String[] submissionParams) {
		assertTrue(logic.getInstructorsForCourse(instructor1OfCourse1.getCourseId()).size() > 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor2OfCourse1.getCourseId(), instructor2OfCourse1.getEmail()));
        assertNotNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getEmail()));
	}

    @Test
    protected void testExecute_adminDeletesLastInstructorByGoogleId_shouldPass() {
        loginAsAdmin();

        InstructorAttributes instructor4 = typicalBundle.instructors.get("instructor4");
        String instructorId = instructor4.getGoogleId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, instructor4.getCourseId(),
        };

        assertEquals(logic.getInstructorsForCourse(instructor4.getCourseId()).size(), 1);

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());

        assertNull(logic.getInstructorForEmail(instructor4.getCourseId(), instructor4.getEmail()));
    }

    @Test
    protected void testExecute_instructorDeleteOwnRoleByGoogleId_shouldPass() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor2OfCourse1.getGoogleId(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        deleteEmailActions(instructor1OfCourse1, instructor2OfCourse1, submissionParams);
    }

    @Test
    protected void testExecute_deleteLastInstructorByGoogleId_shouldFail() {
        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructor4");
        String courseId = instructorToDelete.getCourseId();

        loginAsInstructor(instructorToDelete.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.getGoogleId(),
        };

        assertEquals(logic.getInstructorsForCourse(courseId).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(submissionParams);
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructorToDelete.getCourseId(), instructorToDelete.getEmail()));
        assertNotNull(logic.getInstructorForGoogleId(instructorToDelete.getCourseId(), instructorToDelete.getGoogleId()));
    }

    @Test
    protected void testExecute_deleteLastInstructorInMasqueradeByGoogleId_shouldFail() {
        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructor4");
        String courseId = instructorToDelete.getCourseId();

        loginAsAdmin();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.getGoogleId(),
        };

        assertEquals(logic.getInstructorsForCourse(courseId).size(), 1);

        InvalidOperationException ioe = verifyInvalidOperation(
                addUserIdToParams(instructorToDelete.getGoogleId(), submissionParams));
        assertEquals("The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.", ioe.getMessage());

        assertNotNull(logic.getInstructorForEmail(instructorToDelete.getCourseId(), instructorToDelete.getEmail()));
        assertNotNull(logic.getInstructorForGoogleId(instructorToDelete.getCourseId(), instructorToDelete.getGoogleId()));
    }

    @Test
    protected void testExecute_deleteInstructorInMasqueradeByGoogleId_shouldPass() {
        InstructorAttributes instructorToDelete = typicalBundle.instructors.get("instructorNotDisplayedToStudent2");
        String courseId = instructorToDelete.getCourseId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.INSTRUCTOR_ID, instructorToDelete.getGoogleId(),
        };

        loginAsAdmin();

        assertTrue(logic.getInstructorsForCourse(courseId).size() > 1);

        DeleteInstructorAction deleteInstructorAction =
                getAction(addUserIdToParams(instructorToDelete.getGoogleId(), submissionParams));
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput messageOutput = (MessageOutput) response.getOutput();

        assertEquals("Instructor is successfully deleted.", messageOutput.getMessage());
        assertNull(logic.getInstructorForEmail(courseId, instructorToDelete.getEmail()));
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        String[] onlyInstructorParameter = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
        };

        String[] onlyCourseParameter = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        loginAsAdmin();

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyInstructorParameter);
        verifyHttpParameterFailure(onlyCourseParameter);

        loginAsInstructor(instructorId);

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(onlyInstructorParameter);
        verifyHttpParameterFailure(onlyCourseParameter);
    }

    @Test
    protected void testExecute_noSuchInstructor_shouldFail() {
        loginAsAdmin();

        attemptToDeleteFakeInstructorByGoogleId();
        attemptToDeleteFakeInstructorByEmail();

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        attemptToDeleteFakeInstructorByGoogleId();
        attemptToDeleteFakeInstructorByEmail();
    }

    private void attemptToDeleteFakeInstructorByGoogleId() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, "fake-googleId",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertNull(logic.getInstructorForGoogleId(instructor1OfCourse1.getCourseId(), "fake-googleId"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    private void attemptToDeleteFakeInstructorByEmail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "fake-instructor@fake-email",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        assertNull(logic.getInstructorForEmail(instructor1OfCourse1.getCourseId(), "fake-instructor@fake-email"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Test
    protected void testExecute_adminDeletesInstructorInFakeCourse_shouldFail() {
        loginAsAdmin();

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructorId,
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        DeleteInstructorAction deleteInstructorAction = getAction(submissionParams);
        JsonResult response = getJsonResult(deleteInstructorAction);

        MessageOutput msg = (MessageOutput) response.getOutput();
        assertEquals("Instructor is successfully deleted.", msg.getMessage());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);
        verifyAccessibleForAdmin(submissionParams);
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
