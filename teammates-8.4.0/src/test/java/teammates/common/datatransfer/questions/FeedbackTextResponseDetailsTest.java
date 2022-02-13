package teammates.common.datatransfer.questions;

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
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackTextResponseDetails}.
 */
public class FeedbackTextResponseDetailsTest extends BaseTestCase {
    @Test
    public void testValidateArgumentConstructor_sameValues_shouldReturnTrue() {
        String testAnswer = "Hello World";
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(testAnswer);
        assertEquals(feedbackTextResponseDetails.getAnswer(), "Hello World");
    }

    @Test
    public void testValidateAttributesOfAnswer_nullValue_shouldReturnNull() {
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(null);
        assertNull(feedbackTextResponseDetails.getAnswer());
    }

    @Test
    public void testValidateSanitizedAnswerString_nullValues_shouldReturnTrue() {
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(null);
        assertEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(feedbackTextResponseDetails.getAnswer()));
    }

    @Test
    public void testValidateSanitizedAnswerString_validValues_shouldReturnTrue() {
        String testValue = "Hello World";
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(testValue);
        assertEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(feedbackTextResponseDetails.getAnswer()));
    }

    @Test
    public void testValidateSetAnswer_sameValues_shouldReturnTrue() {
        String testValue = "Hello World";
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails();
        assertNotEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(testValue));

        feedbackTextResponseDetails.setAnswer(testValue);

        assertEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(testValue));
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
