package teammates.common.datatransfer.questions;

import java.util.Arrays;
import java.util.List;

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
 * SUT: {@link FeedbackNumericalScaleQuestionDetails}.
 */
public class FeedbackNumericalScaleQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }

    @Test
    public void tesValidateResponseDetails() {
        FeedbackNumericalScaleQuestionDetails numScaleQuestion = new FeedbackNumericalScaleQuestionDetails();
        numScaleQuestion.setStep(0.1);

        ______TS("Test Val=1.2 and Step=0.1 does no trigger error");
        FeedbackNumericalScaleResponseDetails resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(1.2);
        List<String> errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertTrue(errors.isEmpty());

        ______TS("Test Val=1.22 and Step=0.1 triggers error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(1.22);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertEquals(1, errors.size());
        assertEquals("Please enter a valid value. The two nearest valid values are 1.2 and 1.3.", errors.get(0));

        ______TS("Test Val=1.333 and Step=0.1 triggers error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(1.333);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertEquals(1, errors.size());
        assertEquals("Please enter a valid value. The two nearest valid values are 1.3 and 1.4.", errors.get(0));

        ______TS("Test Val=2 and Step=0.1 does not trigger error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(2);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertTrue(errors.isEmpty());

        numScaleQuestion.setStep(0.00001);

        ______TS("Test Val=1.33333 and Step=0.00001 does not trigger error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(1.33333);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertTrue(errors.isEmpty());

        ______TS("Test Val=1.333333 and Step=0.00001 triggers error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(1.333333);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertEquals(1, errors.size());
        assertEquals("Please enter a valid value. The two nearest valid values are 1.33333 and 1.33334.",
                errors.get(0));

        numScaleQuestion.setStep(0.7);
        numScaleQuestion.setMinScale(3);
        numScaleQuestion.setMaxScale(5);

        ______TS("Test Val=6 and Max=5,Step=0.7 triggers error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(5.1);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertEquals(1, errors.size());
        assertEquals("5.1 is out of the range for Numerical-scale question.(min=3, max=5)", errors.get(0));

        ______TS("Test Val=0 and Min=3,Step=0.7 triggers error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(5.1);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertEquals(1, errors.size());
        assertEquals("5.1 is out of the range for Numerical-scale question.(min=3, max=5)", errors.get(0));

        ______TS("Test Val=5.1 and Max=5,Step=0.7 triggers error");
        resp = new FeedbackNumericalScaleResponseDetails();
        resp.setAnswer(5.1);
        errors = numScaleQuestion.validateResponsesDetails(Arrays.asList(resp), 1);
        assertEquals(1, errors.size());
        assertEquals("5.1 is out of the range for Numerical-scale question.(min=3, max=5)", errors.get(0));

        FeedbackNumericalScaleResponseDetails correctResp = new FeedbackNumericalScaleResponseDetails();
        correctResp.setAnswer(3);

        FeedbackNumericalScaleResponseDetails respInvalidStep = new FeedbackNumericalScaleResponseDetails();
        respInvalidStep.setAnswer(3.5);

        FeedbackNumericalScaleResponseDetails respInvalidRange = new FeedbackNumericalScaleResponseDetails();
        respInvalidRange.setAnswer(100);

        ______TS("Test 1 correct + 2 wrong triggers right messages");
        errors = numScaleQuestion
                .validateResponsesDetails(Arrays.asList(correctResp, respInvalidStep, respInvalidRange), 1);
        assertEquals(2, errors.size());
        assertEquals("Please enter a valid value. The two nearest valid values are 3.0 and 3.7.", errors.get(0));
        assertEquals("100 is out of the range for Numerical-scale question.(min=3, max=5)", errors.get(1));
    }

    @Test
    public void tesValidateQuestionDetails() {
        FeedbackNumericalScaleQuestionDetails numScaleQuestion = new FeedbackNumericalScaleQuestionDetails();

        ______TS("Test Default no error");
        List<String> errors = numScaleQuestion.validateQuestionDetails();
        assertTrue(errors.isEmpty());

        ______TS("Test Min greater than Max error");
        numScaleQuestion.setMaxScale(1);
        numScaleQuestion.setMinScale(10);
        errors = numScaleQuestion.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackNumericalScaleQuestionDetails.NUMSCALE_ERROR_MIN_MAX, errors.get(0));

        ______TS("Test Step <= 0 error");
        numScaleQuestion.setMaxScale(100);
        numScaleQuestion.setStep(0);
        errors = numScaleQuestion.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackNumericalScaleQuestionDetails.NUMSCALE_ERROR_STEP, errors.get(0));

        ______TS("Test Step <= 0 + Min >= Max error");
        numScaleQuestion.setMinScale(100);
        errors = numScaleQuestion.validateQuestionDetails();
        assertEquals(2, errors.size());
        assertEquals(FeedbackNumericalScaleQuestionDetails.NUMSCALE_ERROR_MIN_MAX, errors.get(0));
        assertEquals(FeedbackNumericalScaleQuestionDetails.NUMSCALE_ERROR_STEP, errors.get(1));
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
