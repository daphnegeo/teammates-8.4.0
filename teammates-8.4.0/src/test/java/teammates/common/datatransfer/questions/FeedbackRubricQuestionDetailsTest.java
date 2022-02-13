package teammates.common.datatransfer.questions;

import java.util.ArrayList;
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
 * SUT: {@link FeedbackRubricQuestionDetails}.
 */
public class FeedbackRubricQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();

        assertEquals(FeedbackQuestionType.RUBRIC, rubricDetails.getQuestionType());
        assertFalse(rubricDetails.isHasAssignedWeights());
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testValidateQuestionDetails_invalidWeightListSize_errorReturned() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setNumOfRubricChoices(2);
        rubricDetails.setNumOfRubricSubQuestions(2);
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", ""), Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(true);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(1.5, 2.5), Collections.singletonList(1.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_validWeightListSize_errorListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setNumOfRubricChoices(2);
        rubricDetails.setNumOfRubricSubQuestions(2);
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", ""), Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(true);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(1.5, 2.5), Arrays.asList(1.0, 2.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateResponseDetails_validAnswer_shouldReturnEmptyErrorList() {
        FeedbackRubricQuestionDetails rubricQuestionDetails = validateAnswermethod();

        FeedbackRubricResponseDetails responseDetails = new FeedbackRubricResponseDetails();

        responseDetails.setAnswer(Arrays.asList(1, FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN));
        assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN, 0));
        assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 0));
        assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());
    }

	/**
	 * @return
	 */
	private FeedbackRubricQuestionDetails validateAnswermethod() {
		FeedbackRubricQuestionDetails rubricQuestionDetails = new FeedbackRubricQuestionDetails();
        rubricQuestionDetails.setHasAssignedWeights(false);
        rubricQuestionDetails.setRubricWeightsForEachCell(new ArrayList<>());
        rubricQuestionDetails.setNumOfRubricChoices(2);
        rubricQuestionDetails.setNumOfRubricSubQuestions(2);
        rubricQuestionDetails.setRubricChoices(Arrays.asList("a", "b"));
        rubricQuestionDetails.setRubricSubQuestions(Arrays.asList("q1", "q2"));
        rubricQuestionDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("d1", "d2"), Arrays.asList("d3", "d4")));
		return rubricQuestionDetails;
	}

    @Test
    public void testValidateResponseDetails_invalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackRubricQuestionDetails rubricQuestionDetails = validateAnswermethod();

        FeedbackRubricResponseDetails responseDetails = new FeedbackRubricResponseDetails();

        responseDetails.setAnswer(Arrays.asList());
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN,
                FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, -2));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(2, 1));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 1, 0));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRubricQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRubricQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
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
