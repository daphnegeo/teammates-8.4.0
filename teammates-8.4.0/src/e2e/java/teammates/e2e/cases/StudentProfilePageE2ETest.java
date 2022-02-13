package teammates.e2e.cases;

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
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentProfilePage;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_PROFILE_PAGE}.
 */
public class StudentProfilePageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentProfilePageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {

        ______TS("Typical case: Log in with filled profile values");

        AppUrl url = createUrl(Const.WebPageURIs.STUDENT_PROFILE_PAGE);
        StudentProfilePage profilePage = loginToPage(url, StudentProfilePage.class, "tm.e2e.SProf.student");

        profilePage.ensureProfileContains("Ben", "i.m.benny@gmail.tmt", "TEAMMATES Test Institute 4",
                "Singaporean", StudentProfileAttributes.Gender.MALE, "I am just another student :P");

        ______TS("Typical case: picture upload and edit");

        profilePage.fillProfilePic("src/test/resources/images/profile_pic.png");
        profilePage.uploadPicture();
        profilePage.verifyStatusMessage("Your profile picture has been saved successfully");

        profilePage.showPictureEditor();
        profilePage.waitForUploadEditModalVisible();

        profilePage.editProfilePhoto();
        profilePage.verifyPhotoSize("220px", "220px");

        ______TS("Typical case: edit profile page");
        profilePage.editProfileThroughUi("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
        profilePage.verifyStatusMessage("Your profile has been edited successfully");

        profilePage.ensureProfileContains("short.name", "e@email.tmt", "inst", "American",
                StudentProfileAttributes.Gender.FEMALE, "this is enough!$%&*</>");
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
