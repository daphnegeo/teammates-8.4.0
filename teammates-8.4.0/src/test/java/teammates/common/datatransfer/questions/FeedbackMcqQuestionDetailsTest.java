package teammates.common.datatransfer.questions;

import java.util.Arrays;
import java.util.Collections;
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
 * SUT: {@link FeedbackMcqQuestionDetails}.
 */
public class FeedbackMcqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();

        assertEquals(FeedbackQuestionType.MCQ, mcqDetails.getQuestionType());
        assertFalse(mcqDetails.isHasAssignedWeights());
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testValidateQuestionDetails_choicesLessThanMinRequirement_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setNumOfMcqChoices(1);
        mcqDetails.setMcqChoices(Collections.singletonList("Choice 2"));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMcqQuestionDetails.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_numberOfChoicesGreaterThanWeights_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setNumOfMcqChoices(2);
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setMcqWeights(Collections.singletonList(1.22));
        mcqDetails.setHasAssignedWeights(true);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setNumOfMcqChoices(2);
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setMcqWeights(Arrays.asList(1.22, 1.55));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_negativeWeights_errorsReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setNumOfMcqChoices(2);
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setMcqWeights(Arrays.asList(1.22, -1.55));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorsReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setNumOfMcqChoices(2);
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setMcqWeights(Arrays.asList(1.22, 1.55));
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setMcqOtherWeight(-2);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_duplicateMcqOptions_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();

        mcqDetails.setNumOfMcqChoices(2);
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", "choice 1"));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_DUPLICATE_MCQ_OPTION, errors.get(0));

        //duplicate cases that has trailing and leading spaces
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", " choice 1 "));
        errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_DUPLICATE_MCQ_OPTION, errors.get(0));
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackMcqQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackMcqQuestionDetails();
        assertTrue(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
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
