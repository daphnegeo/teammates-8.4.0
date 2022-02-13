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
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.e2e.util.TestProperties;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE}.
 */
public class InstructorCourseStudentDetailsEditPageE2ETest extends BaseE2ETestCase {
    private StudentAttributes student;
    private StudentAttributes otherStudent;
    private CourseAttributes course;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        student = testData.students.get("ICSDetEdit.jose.tmms");
        otherStudent = testData.students.get("ICSDetEdit.benny.c");
        course = testData.courses.get("ICSDetEdit.CS2104");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl editPageUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE)
                .withCourseId(course.getId())
                .withStudentEmail(student.getEmail());
        InstructorCourseStudentDetailsEditPage editPage =
                loginToPage(editPageUrl, InstructorCourseStudentDetailsEditPage.class,
                        testData.instructors.get("ICSDetEdit.instr").getGoogleId());

        ______TS("verify loaded data");
        editPage.verifyStudentDetails(student);

        ______TS("edit student details");
        student.setName("edited name");
        student.setSection("edited section");
        student.setTeam("edited team");
        student.setComments("edited comment");
        editPage.editStudentDetails(student);

        editPage.verifyStatusMessage("Student has been updated");
        verifyPresentInDatabase(student);

        ______TS("cannot edit to an existing email");
        editPage = getNewPageInstance(editPageUrl, InstructorCourseStudentDetailsEditPage.class);
        editPage.editStudentEmailAndResendLinks(otherStudent.getEmail());

        editPage.verifyStatusMessage("Trying to update to an email that is already in use");

        ______TS("edit email and resend links");
        String newEmail = TestProperties.TEST_EMAIL;
        student.setEmail(newEmail);
        student.setGoogleId(null);
        editPage.editStudentEmailAndResendLinks(newEmail);

        editPage.verifyStatusMessage("Student has been updated and email sent");
        verifyPresentInDatabase(student);
        verifyEmailSent(newEmail, "TEAMMATES: Summary of course ["
                + course.getName() + "][Course ID: " + course.getId() + "]");
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
