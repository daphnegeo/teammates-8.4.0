package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionsVariousAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackVisibilityType;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.request.FeedbackQuestionUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackQuestionAction}.
 */
public class UpdateFeedbackQuestionActionTest extends BaseActionTest<UpdateFeedbackQuestionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Test
    public void testExecute_customizedNumberOfRecipient_shouldUpdateSuccessfully() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionsVariousAttributes typicalQuestion =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), 1);

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
        };
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);
        updateRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(10);

        UpdateFeedbackQuestionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        typicalQuestion = logic.getFeedbackQuestion(typicalQuestion.getId());

        assertEquals(10, typicalQuestion.getNumberOfEntitiesToGiveFeedbackTo());
    }

    @Test
    public void testExecute_anonymousTeamSession_shouldUpdateSuccessfully() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionsVariousAttributes typicalQuestion =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), 1);

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
        };
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.TEAMS);
        updateRequest.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        updateRequest.setShowGiverNameTo(Arrays.asList());
        updateRequest.setShowRecipientNameTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));

        UpdateFeedbackQuestionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        typicalQuestion = logic.getFeedbackQuestion(typicalQuestion.getId());

        assertEquals(FeedbackParticipantType.STUDENTS, typicalQuestion.getGiverType());
        assertEquals(FeedbackParticipantType.TEAMS, typicalQuestion.getRecipientType());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), typicalQuestion.getShowResponsesTo());
        assertTrue(typicalQuestion.getShowGiverNameTo().isEmpty());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), typicalQuestion.getShowRecipientNameTo());
    }

    @Test
    public void testExecute_selfFeedback_shouldUpdateSuccessfully() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes session = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionsVariousAttributes typicalQuestion =
                logic.getFeedbackQuestion(session.getFeedbackSessionName(), session.getCourseId(), 1);

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
        };
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.SELF);
        updateRequest.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));
        updateRequest.setShowGiverNameTo(Arrays.asList());
        updateRequest.setShowRecipientNameTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT));

        UpdateFeedbackQuestionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        typicalQuestion = logic.getFeedbackQuestion(typicalQuestion.getId());

        assertEquals(FeedbackParticipantType.STUDENTS, typicalQuestion.getGiverType());
        assertEquals(FeedbackParticipantType.SELF, typicalQuestion.getRecipientType());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), typicalQuestion.getShowResponsesTo());
        assertTrue(typicalQuestion.getShowGiverNameTo().isEmpty());
        assertEquals(Arrays.asList(FeedbackParticipantType.RECEIVER), typicalQuestion.getShowRecipientNameTo());
    }

    /**
	 * @deprecated Use {@link teammates.logic.api.LogicExtension#testExecute_editingContributionTypeQuestion_shouldUpdateSuccessfully(teammates.ui.webapi.UpdateFeedbackQuestionActionTest)} instead
	 */
	@Test
	public void testExecute_editingContributionTypeQuestion_shouldUpdateSuccessfully() {
		logic.testExecute_editingContributionTypeQuestion_shouldUpdateSuccessfully(this);
	}

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_invalidQuestionNumber_shouldThrowException(teammates.ui.webapi.UpdateFeedbackQuestionActionTest)} instead
	 */
	@Test
	public void testExecute_invalidQuestionNumber_shouldThrowException() {
		typicalBundle.testExecute_invalidQuestionNumber_shouldThrowException(this);
	}

    // TODO: ADD this test case in FeedbackTextQuestionDetailsTest
	/**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_invalidRecommendedLength_shouldThrowException(teammates.ui.webapi.UpdateFeedbackQuestionActionTest)} instead
	 */
	@Test
	public void testExecute_invalidRecommendedLength_shouldThrowException() {
		typicalBundle.testExecute_invalidRecommendedLength_shouldThrowException(this);
	}

    /**
	 * @deprecated Use {@link teammates.common.datatransfer.DataBundle#testExecute_invalidGiverRecipientType_shouldThrowException(teammates.ui.webapi.UpdateFeedbackQuestionActionTest)} instead
	 */
	@Test
	public void testExecute_invalidGiverRecipientType_shouldThrowException() {
		typicalBundle.testExecute_invalidGiverRecipientType_shouldThrowException(this);
	}

    @Test
    public void testExecute_differentScenarios_shouldUpdateResponseRateCorrectly() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        int numStudentRespondents = 4;
        int numInstructorRespondents = 1;

        int totalStudents = 5;
        int totalInstructors = 5;

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Check response rate before editing question 1");

        fs = logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        int submittedTotal = logic.getActualTotalSubmission(fs);
        int expectedTotal = logic.getExpectedTotalSubmission(fs);
        assertEquals(numStudentRespondents + numInstructorRespondents, submittedTotal);
        assertEquals(totalStudents + totalInstructors, expectedTotal);

        ______TS("Change the feedback path of a question with no unique respondents, "
                + "response rate should not be updated");

        FeedbackQuestionsVariousAttributes fq =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);
        FeedbackQuestionUpdateRequest updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setQuestionNumber(fq.getQuestionNumber());
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.STUDENTS);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);
        updateRequest.setCustomNumberOfEntitiesToGiveFeedbackTo(1);

        String[] param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getFeedbackQuestionId(),
        };
        UpdateFeedbackQuestionAction a = getAction(updateRequest, param);
        getJsonResult(a);

        // TODO first comment was there before, but the second one seems to be the one happening?
        // Response rate should not change because other questions have the same respondents
        // Response rate should decrease by 1 as response from student1 in qn1 is changed
        numStudentRespondents--;
        fs = logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        submittedTotal = logic.getActualTotalSubmission(fs);
        expectedTotal = logic.getExpectedTotalSubmission(fs);
        assertEquals(numStudentRespondents + numInstructorRespondents, submittedTotal);
        assertEquals(totalStudents + totalInstructors, expectedTotal);

        ______TS("Change the feedback path of a question with a unique instructor respondent, "
                + "response rate changed");

        fq = logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 3);
        updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setQuestionNumber(fq.getQuestionNumber());
        updateRequest.setGiverType(fq.getGiverType());
        updateRequest.setRecipientType(FeedbackParticipantType.STUDENTS);

        param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getFeedbackQuestionId(),
        };
        a = getAction(updateRequest, param);
        getJsonResult(a);

        // Response rate should decrease by 1 because the response of the unique instructor respondent is deleted
        fs = logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        submittedTotal = logic.getActualTotalSubmission(fs);
        expectedTotal = logic.getExpectedTotalSubmission(fs);
        assertEquals(numStudentRespondents, submittedTotal);
        assertEquals(totalStudents + totalInstructors, expectedTotal);

        ______TS("Change the feedback path of a question so that some possible respondents are removed");

        fq = logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 4);
        updateRequest = getTypicalTextQuestionUpdateRequest();
        updateRequest.setQuestionNumber(fq.getQuestionNumber());
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.NONE);

        param = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getFeedbackQuestionId(),
        };
        a = getAction(updateRequest, param);
        getJsonResult(a);

        // Total possible respondents should decrease because instructors
        // (except session creator) are no longer possible respondents
        fs = logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
        submittedTotal = logic.getActualTotalSubmission(fs);
        expectedTotal = logic.getExpectedTotalSubmission(fs);
        assertEquals(numStudentRespondents, submittedTotal);
        assertEquals(totalStudents + 1, expectedTotal);
    }

    private FeedbackQuestionUpdateRequest getTypicalTextQuestionUpdateRequest() {
        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();
        updateRequest.setQuestionNumber(2);
        updateRequest.setQuestionBrief("this is the brief");
        updateRequest.setQuestionDescription("this is the description");
        FeedbackTextQuestionDetails textQuestionDetails = new FeedbackTextQuestionDetails();
        textQuestionDetails.setRecommendedLength(800);
        updateRequest.setQuestionDetails(textQuestionDetails);
        updateRequest.setQuestionType(FeedbackQuestionType.TEXT);
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.INSTRUCTORS);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        updateRequest.setShowResponsesTo(new ArrayList<>());
        updateRequest.setShowGiverNameTo(new ArrayList<>());
        updateRequest.setShowRecipientNameTo(new ArrayList<>());

        return updateRequest;
    }

    private FeedbackQuestionUpdateRequest getTypicalContributionQuestionUpdateRequest() {
        FeedbackQuestionUpdateRequest updateRequest = new FeedbackQuestionUpdateRequest();
        updateRequest.setQuestionNumber(1);
        updateRequest.setQuestionBrief("this is the brief for contribution question");
        updateRequest.setQuestionDescription("this is the description for contribution question");
        FeedbackContributionQuestionDetails textQuestionDetails = new FeedbackContributionQuestionDetails();
        textQuestionDetails.setNotSureAllowed(false);
        updateRequest.setQuestionDetails(textQuestionDetails);
        updateRequest.setQuestionType(FeedbackQuestionType.CONTRIB);
        updateRequest.setGiverType(FeedbackParticipantType.STUDENTS);
        updateRequest.setRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
        updateRequest.setNumberOfEntitiesToGiveFeedbackToSetting(NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);

        updateRequest.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        updateRequest.setShowGiverNameTo(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        updateRequest.setShowRecipientNameTo(Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));

        return updateRequest;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionsVariousAttributes typicalQuestion =
                logic.getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);

        ______TS("non-existent feedback question");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        verifyEntityNotFoundAcl(Const.ParamsNames.FEEDBACK_QUESTION_ID, "random");

        ______TS("accessible only for instructor with ModifySessionPrivilege");

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalQuestion.getFeedbackQuestionId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, submissionParams);
    }

}
