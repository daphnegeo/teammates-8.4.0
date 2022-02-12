package teammates.logic.api;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionsVariousAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.test.BaseTestCase;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.webapi.UpdateFeedbackQuestionAction;
import teammates.ui.webapi.UpdateFeedbackQuestionActionTest;

/**
 * Holds additional methods for {@link Logic} used only in tests.
 */
public class LogicExtension extends Logic {

    public FeedbackQuestionsVariousAttributes getFeedbackQuestion(
            String feedbackSessionName, String courseId, int questionNumber) {
        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
    }

    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String recipient) {
        return feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Instant creationDate) {
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForResponse(String responseId) {
        return feedbackResponseCommentsLogic.getFeedbackResponseCommentForResponse(responseId);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        return feedbackResponsesLogic.getFeedbackResponsesForQuestion(feedbackQuestionId);
    }

	@Test
	public void testExecute_editingContributionTypeQuestion_shouldUpdateSuccessfully(UpdateFeedbackQuestionActionTest updateFeedbackQuestionActionTest) {
	    DataBundle dataBundle = updateFeedbackQuestionActionTest.loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
	    updateFeedbackQuestionActionTest.removeAndRestoreDataBundle(dataBundle);
	
	    InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
	
	    updateFeedbackQuestionActionTest.loginAsInstructor(instructor1ofCourse1.getGoogleId());
	
	    FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("contribSession");
	    FeedbackQuestionsVariousAttributes fq =
	            getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);
	
	    BaseTestCase.______TS("Edit text won't delete response");
	
	    // There are already responses for this question
	    BaseTestCase.assertFalse(getFeedbackResponsesForQuestion(fq.getId()).isEmpty());
	
	    FeedbackQuestionUpdateRequest updateRequest = updateFeedbackQuestionActionTest.getTypicalContributionQuestionUpdateRequest();
	    updateRequest.setQuestionNumber(fq.getQuestionNumber());
	    updateRequest.setGiverType(fq.getGiverType());
	    updateRequest.setRecipientType(fq.getRecipientType());
	    updateRequest.setQuestionDetails(fq.getQuestionDetailsCopy());
	
	    String[] param = new String[] {
	            ParamsNames.FEEDBACK_QUESTION_ID, fq.getFeedbackQuestionId(),
	    };
	    UpdateFeedbackQuestionAction a = updateFeedbackQuestionActionTest.getAction(updateRequest, param);
	    updateFeedbackQuestionActionTest.getJsonResult(a);
	
	    // All existing responses should remain
	    BaseTestCase.assertFalse(getFeedbackResponsesForQuestion(fq.getId()).isEmpty());
	
	    BaseTestCase.______TS("Edit: Invalid recipient type");
	
	    FeedbackQuestionUpdateRequest request = updateFeedbackQuestionActionTest.getTypicalContributionQuestionUpdateRequest();
	    request.setQuestionNumber(fq.getQuestionNumber());
	    request.setRecipientType(FeedbackParticipantType.STUDENTS);
	    updateFeedbackQuestionActionTest.verifyHttpRequestBodyFailure(request, param);
	}

}
