package teammates.ui.request;

import java.util.ArrayList;
import java.util.Arrays;

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
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentsEnrollRequest}.
 */
public class StudentsEnrollRequestTest extends BaseTestCase {

    @Test
    public void testValidate_withValidRequest_shouldPass() throws Exception {
        StudentsEnrollRequest request = new StudentsEnrollRequest(Arrays.asList(getTypicalStudentEnrollRequest(0)));
        request.validate();
    }

    @Test
    public void testValidate_withNullValueInRequest_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest request =
                new StudentsEnrollRequest.StudentEnrollRequest("typical name", null, "typical team",
                        "typical section", "typical comment");
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(request));
        assertThrows(InvalidHttpRequestBodyException.class, enrollRequest::validate);
    }

    @Test
    public void testValidate_withEmptyEnrollList_shouldFail() {
        StudentsEnrollRequest request = new StudentsEnrollRequest(new ArrayList<>());
        assertThrows(InvalidHttpRequestBodyException.class, request::validate);
    }

    @Test
    public void testValidate_withDuplicateEmail_shouldFail() {
        StudentsEnrollRequest.StudentEnrollRequest requestOne = getTypicalStudentEnrollRequest(0);
        StudentsEnrollRequest.StudentEnrollRequest requestTwo = getTypicalStudentEnrollRequest(0);
        String duplicatedEmail = requestOne.getEmail();
        StudentsEnrollRequest enrollRequest = new StudentsEnrollRequest(Arrays.asList(requestOne, requestTwo));
        InvalidHttpRequestBodyException actualException =
                assertThrows(InvalidHttpRequestBodyException.class, enrollRequest::validate);
        assertEquals(actualException.getMessage(),
                "Error, duplicated email addresses detected in the input: " + duplicatedEmail);
    }

    private StudentsEnrollRequest.StudentEnrollRequest getTypicalStudentEnrollRequest(int index) {
        return new StudentsEnrollRequest.StudentEnrollRequest("typical name",
                String.format("typical%d@email.com", index), "typical team",
                "typical section", "typical comment");
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
