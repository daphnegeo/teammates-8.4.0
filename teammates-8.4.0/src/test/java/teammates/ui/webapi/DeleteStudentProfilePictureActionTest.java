package teammates.ui.webapi;

import org.testng.annotations.BeforeClass;
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
 * SUT: {@link DeleteStudentProfilePictureAction}.
 */
public class DeleteStudentProfilePictureActionTest extends BaseActionTest<DeleteStudentProfilePictureAction> {

    private EntityAttributes<Account> account;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeClass
    public void classSetup() {
        account = typicalBundle.accounts.get("student1InCourse1");
    }

    private void testValidAction() throws Exception {
        ______TS("Typical case: success scenario");

        loginAsStudent(account.getGoogleId());

        writeFileToStorage(account.getGoogleId(), "src/test/resources/images/profile_pic.png");
        assertTrue(doesFileExist(account.getGoogleId()));

        String[] submissionParams = {
                Const.ParamsNames.STUDENT_ID, account.getGoogleId(),
        };
        DeleteStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals(messageOutput.getMessage(), "Your profile picture has been deleted successfully");

        assertFalse(doesFileExist(account.getGoogleId()));
    }

    private void testInvalidProfileAction() {
        ______TS("Typical case: invalid student profile");

        loginAsStudent(account.getGoogleId());
        String[] submissionParams = {
                Const.ParamsNames.STUDENT_ID, "invalidGoogleId",
        };
        EntityNotFoundException enfe = verifyEntityNotFound(submissionParams);
        assertEquals("Invalid student profile", enfe.getMessage());
    }

    @Test
    @Override
    protected void testAccessControl() {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
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
