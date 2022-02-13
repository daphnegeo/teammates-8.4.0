package teammates.ui.webapi;

import org.testng.annotations.BeforeMethod;
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

/**
 * SUT: {@link RegenerateStudentKeyAction}.
 */
public class RegenerateStudentKeyActionTest extends BaseActionTest<RegenerateStudentKeyAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    protected void testExecute_notEnoughParameters() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null student email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student1InCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(logic.getCourse("non-existent-course"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_nonExistentStudentInCourse_shouldFail() {
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");

        ______TS("student with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@abc.com",
                Const.ParamsNames.COURSE_ID, student1InCourse1.getCourse(),
        };

        assertNull(logic.getStudentForEmail(student1InCourse1.getCourse(), "non-existent-student@abc.com"));

        verifyEntityNotFound(nonExistingParams);
    }

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_regenerateStudentKey(teammates.ui.webapi.RegenerateStudentKeyActionTest)} instead
	 */
	@Test
	protected void testExecute_regenerateStudentKey() {
		typicalBundle.testExecute_regenerateStudentKey(this);
	}

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
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
